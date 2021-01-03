package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class Userdao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method receives UserEntity object.
     * This method is used to create a new user and store in the users table in the database.
     */
    /**
     * @param userEntity - UserEntity object
     * @return - saved UserEntity object
     */
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * This method receives email of the user.
     * This method is used to fetch the user from the database based on user's email id from users table.
     */
    /**
     * @param email - email of the user
     * @return -  UserEntity object
     */
    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives userName of the user.
     * This method is used to fetch user from the database based on the user's userName from users table.
     */
    /**
     * @param userName - userName of the user
     * @return -  UserEntity object
     */
    public UserEntity getUserByUserName(final String userName) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("userName", userName).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives uuid of the user.
     * This method is used to fetch user from the database based on the user's uuid from users table.
     */
    /**
     * @param uuid - uuid of the user
     * @return -  UserEntity object
     */
    public UserEntity getUserByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method receives UserEntity object.
     * This method is used to delete the users from the users and its child tables.
     */
    /**
     * @param userEntity - UserEntity object
     * @return -  UserEntity object
     */
    public UserEntity deleteUser(UserEntity userEntity) {
        entityManager.remove(userEntity);
        return userEntity;
    }

    /**
     * This method receives UserAuthTokenEntity object.
     * This method is used to create an accessToken and store the user login info in the user_auth table in the database.
     */
    /**
     * @param userAuthTokenEntity - UserAuthTokenEntity object
     * @return - saved UserAuthTokenEntity object
     */
    public UserAuthTokenEntity createAuthToken(UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    /**
     * This method receives accessToken provided by the user.
     * This method is used to fetch the userAuthToken info based on the accessToken provided by the user from user_auth table.
     */
    /**
     * @param accessToken - User accessToken
     * @return -  UserAuthTokenEntity object
     */
    public UserAuthTokenEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userByAccessToken",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
