package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationBusinessService;
import com.upgrad.quora.service.business.SignUpBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.hibernate.id.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private SignUpBusinessService signUpBusinessService;

    @Autowired
    private AuthenticationBusinessService authenticationBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signUp(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("abc1234");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = signUpBusinessService.signUp(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse()
                                                        .id(createdUserEntity.getUuid())
                                                        .status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> loginUser(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        //Eg: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
        //above is a sample encoded text where the username is "username" and password is "password" seperated by a ":"
        String[] authorizationArray = authorization.split("Basic ");
        byte[] decodedByteArray = Base64.getDecoder().decode(authorizationArray[1]);
        String decodedText = new String(decodedByteArray);
        String[] decodedTextArray = decodedText.split(":");

        UserAuthTokenEntity userAuthToken = authenticationBusinessService.authenticateUser(decodedTextArray[0],decodedTextArray[1]);
        UserEntity user = userAuthToken.getUser();

        SigninResponse signinResponse = new SigninResponse()
                                                .id(user.getUuid())
                                                .message("SIGNED IN SUCCESSFULLY");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access_token",userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signoutUser(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthToken = authenticationBusinessService.signoutUser(authorization);
        UserEntity user = userAuthToken.getUser();

        SignoutResponse signoutResponse = new SignoutResponse()
                                                    .id(user.getUuid())
                                                    .message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
