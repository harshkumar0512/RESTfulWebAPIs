package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.Userdao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class UserBusinessService {
    @Autowired
    private Userdao userdao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * This method receives UserEntity object.
     * This method is verifies the userName and email passed by the user, if there is no duplication it calls the user
      repository method else throws exceptions.
     */
    /**
     * @param userEntity - UserEntity object
     * @return - saved UserEntity object
     * @exception - SignUpRestrictedException, if the userName or user email already exists in the database.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException {
        // If the userName already exists in the database
        if (userdao.getUserByUserName(userEntity.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }

        // If the user email already exists in the database
        if (userdao.getUserByEmail(userEntity.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }

        // Encrypt the password using PasswordCryptographyProvider class and save the encrypted password and salt
        String[] encryptedPasswordArray = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedPasswordArray[0]);
        userEntity.setPassword(encryptedPasswordArray[1]);

        return userdao.createUser(userEntity);
    }

    /**
     * This method receives userName and password of the user.
     * This method is used to login the user and create user accessToken, if the user is not present in the database exception in thrown
        else userAuthToken object is created .
     */
    /**
     * @param userName - userName provided by the user
     * @param password - password provided by the user
     * @return - UserAuthTokenEntity object
     * @exception - AuthenticationFailedException, if userName or password authentication fails.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticateUser(final String userName, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userdao.getUserByUserName(userName);

        // If userName authentication fails
        if (userEntity == null) {
            // throw exception
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password,userEntity.getSalt());

        // Check if the encrypted password matches with the password stored in the database
        if (encryptedPassword.equals(userEntity.getPassword())) {
            // Create a JWT token and save the userAuthTokenEntity in user_auth table
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();

            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            userAuthTokenEntity.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));
            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);
            userdao.createAuthToken(userAuthTokenEntity);

            return userAuthTokenEntity;
        } else {
            // If password authentication fails
            // throw exception
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }

    /**
     * This method receives accessToken provided by the user in the authorization header.
     * This method is used to log out the user, if the user is not signed it exception is thrown.
     */
    /**
     * @param accessToken - accessToken of the user
     * @return -  UserAuthTokenEntity object
     * @exception - SignOutRestrictedException, if the user is not signed in.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signoutUser(final String accessToken) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = userdao.getUserAuthToken(accessToken);
        //If the user is not signed in
        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        ZonedDateTime logoutTime = ZonedDateTime.now();
        userAuthTokenEntity.setLogoutAt(logoutTime);
        return userAuthTokenEntity;
    }

    /**
     * This method receives authorization provided by the user in the authorization header.
     * This method is used to check Basic authentication if the authentication format is right, if not then an exception is thrown.
     */
    /**
     * @param authorization - Basic authentication
     * @return -  String
     * @exception - AuthenticationFailedException, if the authentication format is wrong.
     */
    public String getDecodedAuthorizationToken(final String authorization) throws AuthenticationFailedException {
        if (authorization.contains("Basic ")) {
            String[] authorizationArray = authorization.split("Basic ");
            byte[] decodedByteArray = Base64.getDecoder().decode(authorizationArray[1]);
            String decodedText = new String(decodedByteArray);
            return decodedText;
        }
        throw new AuthenticationFailedException("ATH-001","The authentication format is incorrect. The correct format is Basic username:password.");
    }
}
