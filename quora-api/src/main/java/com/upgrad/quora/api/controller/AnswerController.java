package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired private AnswerService answerService;
    /**
     * This endpoint create answer to a question
     *
     * @param questionId : Question ID that you want to answer
     * @param accessToken : access-token to authenticate
     * @param answerRequest : The answer body
     * @throws AuthorizationFailedException : Returns authorization failed exception
     * @throws InvalidQuestionException : If question id is invalid returns invalid question response
     * @return answer creation resopnse
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/question/{questionId}/answer/create",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("questionId") final String questionId,
            AnswerRequest answerRequest)
            throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity = answerService.createAnswer(answerEntity, accessToken, questionId);
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setId(answerEntity.getUuid());
        answerResponse.setStatus("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * This endpoint update the answer
     *
     * @param answerId : Answer ID that you want to Update
     * @param accessToken : access-token to authenticate
     * @throws AuthorizationFailedException : Returns authorization failed exception
     * @throws AnswerNotFoundException : If answer id is invalid returns invalid answer response
     * @return answer Updated response
     */
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/answer/edit/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("answerId") final String answerId,
            AnswerEditRequest answerEditRequest)
            throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        AnswerEntity answerEntity =
                answerService.editAnswer(accessToken, answerId, answerEditRequest.getContent());
        answerEditResponse.setId(answerEntity.getUuid());
        answerEditResponse.setStatus("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }
    /**
     * This endpoint delete answer
     *
     * @param answerId : Answer ID that you want to Delete
     * @param accessToken : access-token to authenticate
     * @throws AuthorizationFailedException : Returns authorization failed exception
     * @throws AnswerNotFoundException : If answer id is invalid returns invalid answer id response
     * @return answer deleted response
     */
    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("answerId") String answerId)
            throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = answerService.deleteAnswer(answerId, accessToken);
        AnswerDeleteResponse answerDeleteResponse =
                new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }
    /**
     * This endpoint get all the answer to a question
     *
     * @param questionId : Answer ID that you want to Delete
     * @param accessToken : access-token to authenticate
     * @throws AuthorizationFailedException : Returns authorization failed exception
     * @throws InvalidQuestionException : If Question id is invalid returns invalid Question id
     *     response
     * @return All the answer to the Question
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/answer/all/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("questionId") String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {
        List<AnswerEntity> answers = answerService.getAllAnswersToQuestion(questionId, accessToken);
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
        for (AnswerEntity answerEntity : answers) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setId(answerEntity.getUuid());
            answerDetailsResponse.setQuestionContent(answerEntity.getQuestionEntity().getContent());
            answerDetailsResponse.setAnswerContent(answerEntity.getAnswer());
            answerDetailsResponses.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);
    }
}
