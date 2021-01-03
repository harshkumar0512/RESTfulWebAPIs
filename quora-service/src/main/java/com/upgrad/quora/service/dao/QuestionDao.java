package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public UserAuthTokenEntity getUserAuthToken(final String accesstoken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accesstoken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestion(final String questionUuid) {
        try {
            return entityManager.createNamedQuery("QuestionEntityByUuid", QuestionEntity.class).setParameter("uuid", questionUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<QuestionEntity> getAllQuestion(final String questionUuid) {
        try {
            return entityManager.createNamedQuery("QuestionEntityByUuid", QuestionEntity.class).setParameter("uuid", questionUuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestionById(final long Id) {
        try {
            return entityManager.createNamedQuery("QuestionEntityByid", QuestionEntity.class).setParameter("id", Id).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<QuestionEntity> getQuestionByUserId(final UserEntity userEntity){
        try{
            return entityManager.createNamedQuery("QuestionEntityByUserId", QuestionEntity.class).setParameter("user_id", userEntity.getId()).getResultList();
        }catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }
}
