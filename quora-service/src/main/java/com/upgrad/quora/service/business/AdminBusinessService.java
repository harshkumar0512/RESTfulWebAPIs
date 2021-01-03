package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.Userdao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private Userdao userdao;

    /**
     * This method receives user's Uuid and accessToken passed in the authorization header.
     * This method is verifies the uuid and accessToken, and deletes the user from the user's and its child tables.
     */
    /**
     * @param userUuid - User userUuid
     * @param accessToken - User accessToken
     * @return -  UserEntity object
     * @exception - AuthorizationFailedException, user has not signed in or user has already signed out.
     * @exception - UserNotFoundException, if the user with the corresponding uuid is not found in the user's table
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userUuid, final String accessToken) throws AuthorizationFailedException,UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userdao.getUserAuthToken(accessToken);
        // If the user is not signed in
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        // If the user has signed out
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        } else {
            String userRole = userAuthTokenEntity.getUser().getRole();

            // If the user is a non-admin user
            if (userRole.toLowerCase().equals("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
            } else {

                UserEntity userEntity = userdao.getUserByUuid(userUuid);
                // If the user is not found in the user's table
                if (userEntity == null) {
                    throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
                }
                // Delete user
                userdao.deleteUser(userEntity);
                return userEntity;
            }
        }
    }

}
