package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    /**
     * This method receives user's Uuid and accessToken passed in the authorization header.
     * This method is verifies the uuid and accessToken, and it fetches the user profile details from the user's table else throws exceptions.
     */
    /**
     * @param userUuid - User userUuid
     * @param accessToken - User accessToken
     * @return -  UserEntity object
     * @exception - AuthorizationFailedException, user has not signed in or user has already signed out.
     * @exception - UserNotFoundException, if the user with the corresponding uuid is not found in the user's table
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity fetchUser(final String userUuid, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        // If the user is not signed in
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        // If the user has signed out
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        } else {
            UserEntity userEntity = userDao.getUserByUuid(userUuid);

            if (userEntity == null) {
                throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
            }
            if (userAuthTokenEntity.getUser().getUuid().equals(userUuid)) {
                return userEntity;
            } else {
                // If the user uuid and the uuid from the user object stored in UserAuthTokenEntity are different
                throw new UserNotFoundException("USR-002","The authorization token does not belong to the user");
            }
        }
    }
}
