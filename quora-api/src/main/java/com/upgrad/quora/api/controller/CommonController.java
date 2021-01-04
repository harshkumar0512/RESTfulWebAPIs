package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * This method receives user's Uuid and accessToken passed in the authorization header.
     * This method is verifies the uuid and accessToken, and fetched the user profile details.
     */
    /**
     * @param userUuid - User userUuid
     * @param authorization - accessToken received from the request header
     * @return -  ResponseEntity object
     * @exception - AuthorizationFailedException
     * @exception - UserNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUserProfileDetails(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        final UserEntity user = commonBusinessService.fetchUser(userUuid, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .emailAddress(user.getEmail())
                .country(user.getCountry())
                .aboutMe(user.getAboutMe())
                .dob(user.getDob())
                .contactNumber(user.getContactNumber());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

}
