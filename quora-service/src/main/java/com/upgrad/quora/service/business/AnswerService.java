package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerService {

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;
    /**
     * Add answer into the database
     *
     * @param questionId : questionid that you want to answer
     * @param answerEntity : the answer body
     * @param accessToken : access-token for authentication
     * @throws AuthorizationFailedException : if authentication is failed
     * @throws InvalidQuestionException : if question id is invalid
     * @return returns created response for the answer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(
            AnswerEntity answerEntity, final String accessToken, final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setQuestionEntity(questionEntity);
        answerEntity.setUserEntity(userAuthEntity.getUserEntity());
        return answerDao.createAnswer(answerEntity);
    }
    /**
     * Update answer into the database
     *
     * @param answerId : questionid that you want to answer
     * @param newAnswer : the answer body
     * @param accessToken : access-token for authentication
     * @throws AuthorizationFailedException : if authentication is failed
     * @throws AnswerNotFoundException : if answer id is invalid
     * @return returns updated response for the answer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(
            final String accessToken, final String answerId, final String newAnswer)
            throws AnswerNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if (!answerEntity.getUserEntity().getUuid().equals(userAuthEntity.getUserEntity().getUuid())) {
            throw new AuthorizationFailedException(
                    "ATHR-003", "Only the answer owner can edit the answer");
        }
        answerEntity.setAnswer(newAnswer);
        answerDao.updateAnswer(answerEntity);
        return answerEntity;
    }
    /**
     * Delete answer from the database
     *
     * @param answerId : answerId of the answer that you want to delete
     * @param accessToken : access-token for authentication
     * @throws AuthorizationFailedException : if authentication is failed
     * @throws AnswerNotFoundException : if answer id is invalid
     * @return returns deleted response for the answer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String answerId, final String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if (userAuthEntity.getUserEntity().getRole().equals("admin")
                || answerEntity
                .getUserEntity()
                .getUuid()
                .equals(userAuthEntity.getUserEntity().getUuid())) {
            return answerDao.deleteAnswer(answerId);
        } else {
            throw new AuthorizationFailedException(
                    "ATHR-003", "Only the answer owner or admin can delete the answer");
        }
    }

    /**
     * Get all answer from the database
     *
     * @param questionId : questionid of which you want to see all answers
     * @param accessToken : access-token for authentication
     * @throws AuthorizationFailedException : if authentication is failed
     * @throws InvalidQuestionException : if question id is invalid
     * @return returns all the answers to that question
     */
    public List<AnswerEntity> getAllAnswersToQuestion(
            final String questionId, final String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to get the answers");
        }
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException(
                    "QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }
        return answerDao.getAllAnswersToQuestion(questionId);
    }
}
