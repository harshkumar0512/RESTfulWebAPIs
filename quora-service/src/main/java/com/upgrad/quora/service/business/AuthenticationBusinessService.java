package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.Userdao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthenticationBusinessService {

    @Autowired
    private Userdao userdao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticateUser(final String userName, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userdao.getUserByUserName(userName);

        if (userEntity == null) {
            // throw exception
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password,userEntity.getSalt());

        if (encryptedPassword.equals(userEntity.getPassword())) {
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
            // throw exception
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signoutUser(final String accessToken) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = userdao.getUserAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        ZonedDateTime logoutTime = ZonedDateTime.now();
        userAuthTokenEntity.setLogoutAt(logoutTime);
        return userAuthTokenEntity;
    }

}
