package com.upgrad.quora.api.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommonControllerTest {

    @Autowired
    private MockMvc mvc;

    //This test case passes when you try to get the details of the existing user and the JWT token entered exists in the database and the user corresponding to that JWT token is signed in.
    @Test
    public void details() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/07d91df5-b720-4a05-8f8b-718ff872851c").header("authorization", "eyJraWQiOiJkZDJjOTk1Ni03ODA2LTQwYWUtYjRmOC0yMmU1OTJlYjdmNGMiLCJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiIwN2Q5MWRmNS1iNzIwLTRhMDUtOGY4Yi03MThmZjg3Mjg1MWMiLCJpc3MiOiJodHRwczovL3F1b3JhLmlvIiwiZXhwIjoxNjA5NjkyLCJpYXQiOjE2MDk2NjN9.JsEi-3DrclYThqvx5XR9yP5C8H0tDhVAjNWQ_lilokyBTbBwY7HmWuYZMvC_hxXJBbmpUxCwD0_Iq33T5_ShVQ"))
                .andExpect(status().isOk());
    }

    //This test case passes when you try to get the details of the existing user but the JWT token entered does not exist in the database.
    @Test
    public void detailsUsingNonExistingAccessToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/database_uuid1").header("authorization", "non_existing_access_token"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("ATHR-001"));
    }

    //This test case passes when you try to get the details of the existing user but the user has logged out.
    @Test
    public void detailsOfLoggedOutUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/07d91df5-b720-4a05-8f8b-718ff872851c").header("authorization", "eyJraWQiOiIwNDViYjY0Ni1mZDQ3LTRlYzgtODUzMy0zMWY3OWZjZDQ0Y2YiLCJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiIwN2Q5MWRmNS1iNzIwLTRhMDUtOGY4Yi03MThmZjg3Mjg1MWMiLCJpc3MiOiJodHRwczovL3F1b3JhLmlvIiwiZXhwIjoxNjA5NjQ2LCJpYXQiOjE2MDk2MTd9.RMzb5F458Bp3gsIhnLrPUP6aY0UKsmXHRKTRgdMgaWH_hd-dqgRn99Hf9yIgvp3UVhcLhgkOQMhVoz3ePcMFHA"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("ATHR-002"));
    }

    //This test case passes when you try to get the details of the user which does not exist in the database.
    @Test
    public void detailsOfNonExistingUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/non_existing_user").header("authorization", "database_accesstoken"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("USR-001"));
    }

    // This test cases passes when there is a mismatch in the uuid of the user and the uuid of the user object stored in the UserAuthToken object
    @Test
    public void userUuidMismatch() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/database_uuid2").header("authorization", "database_accesstoken"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("USR-002"));
    }
}
