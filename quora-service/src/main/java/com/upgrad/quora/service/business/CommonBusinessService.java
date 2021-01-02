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
public class CommonBusinessService {

    @Autowired
    private Userdao userdao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity fetchUser(final String userUuid, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userdao.getUserAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        } else {
             UserEntity userEntity = userdao.getUserByUuid(userUuid);

             if (userEntity == null) {
                 throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
             }
             return userEntity;
        }
    }
}
