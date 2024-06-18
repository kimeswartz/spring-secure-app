package com.useo.securewebapplication;

import com.useo.securewebapplication.model.MyUser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
    @AutoConfigureMockMvc
    public class UserManagementControllerTest {

    @Autowired
    private MyUserRepository userRepository;

        @Autowired
        private WebApplicationContext context;

        private MockMvc mvc;

        @BeforeEach
        public void setup() {
            mvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();
        }

    @Test
    public void testFirstPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    @WithMockUser(username = "admin", password = "123", roles = "ADMIN")
    public void testRegistrationFunction_Success() throws Exception {
        // Valid data
        MyUserDto userDto = new MyUserDto();
        userDto.setUsername("test");
        userDto.setPassword("password");
        userDto.setEmail("testuser@example.com");

        mvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", userDto.getUsername())
                        .param("password", userDto.getPassword())
                        .param("email", userDto.getEmail())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("registration_success"));
    }

    @Test
    @WithMockUser(username = "admin", password = "123", roles = "ADMIN")
    public void testRegistrationFunction_Failure_UsernameExists() throws Exception {
        // Create an already existing user
        MyUser existingUser = new MyUser();
        existingUser.setUsername("test");
        existingUser.setPassword("existingPassword");
        existingUser.setEmail("first@example.com");
        userRepository.save(existingUser); // Save using the repository instance

        // Try to register with the same username
        MyUserDto userDto = new MyUserDto();
        userDto.setUsername("test");
        userDto.setPassword("password");
        userDto.setEmail("second@example.com");

        mvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", userDto.getUsername())
                        .param("password", userDto.getPassword())
                        .param("email", userDto.getEmail())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Expecting HTTP 200 OK because of error handling
                .andExpect(MockMvcResultMatchers.view().name("registration_error"));
    }


    @Test
    @WithMockUser(username = "admin", password = "123", roles = "ADMIN")
    public void testDeleteUser_Success() throws Exception {
        // Create a user to delete
        MyUser userToDelete = new MyUser();
        userToDelete.setUsername("userToDelete");
        userToDelete.setPassword("password");
        userToDelete.setEmail("userToDelete@example.com");
        userRepository.save(userToDelete);

        // Perform deletion with matching usernames
        mvc.perform(MockMvcRequestBuilders.post("/delete")
                        .param("username", "userToDelete")
                        .param("confirmUsername", "userToDelete")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("delete_success"));

        // Verify user is deleted
        boolean userExists = userRepository.findByUsername("userToDelete").isPresent();
        Assertions.assertFalse(userExists, "User should have been deleted");
    }

    @Test
    @WithMockUser(username = "admin", password = "123", roles = "ADMIN")
    public void testDeleteUser_Failure_UsernameMismatch() throws Exception {

        // Create my user to delete
        MyUser userToDelete = new MyUser();
        userToDelete.setUsername("userToDelete");
        userToDelete.setPassword("password");
        userToDelete.setEmail("userToDelete@example.com");
        userRepository.save(userToDelete);

        // Deletion with mismatched usernames
        mvc.perform(MockMvcRequestBuilders.post("/delete")
                        .param("username", "userToDelete")
                        .param("confirmUsername", "differentUsername")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Adjust this as per your error handling
                .andExpect(MockMvcResultMatchers.view().name("error_page"));

        // Verify user still exists
        boolean userExists = userRepository.findByUsername("userToDelete").isPresent();
        Assertions.assertTrue(userExists, "User should not have been deleted due to mismatched usernames");
    }

}