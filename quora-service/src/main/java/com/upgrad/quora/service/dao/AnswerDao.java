package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create answers in the DB
     *
     * @param answerEntity : require answer body
     */
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * get answers from the DB
     *
     * @param answerId : require answerid
     */
    public AnswerEntity getAnswerById(final String answerId) {
        try {
            return entityManager
                    .createNamedQuery("getAnswerById", AnswerEntity.class)
                    .setParameter("uuid", answerId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Update answers in the DB
     *
     * @param answerEntity : require answer body
     */
    public void updateAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
    }

    /**
     * delete answers from the DB
     *
     * @param answerId : require answerId
     */
    public AnswerEntity deleteAnswer(final String answerId) {
        AnswerEntity deleteAnswer = getAnswerById(answerId);
        if (deleteAnswer != null) {
            entityManager.remove(deleteAnswer);
        }
        return deleteAnswer;
    }

    // fetch all the answers to the question using questionId
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId) {
        return entityManager
                .createNamedQuery("getAllAnswersToQuestion", AnswerEntity.class)
                .setParameter("uuid", questionId)
                .getResultList();
    }
}
