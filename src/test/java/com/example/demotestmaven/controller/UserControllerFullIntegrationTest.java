package com.example.demotestmaven.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.dto.RoleDTO;
import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@Sql(scripts = "/data.sql")
public class UserControllerFullIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private String usernameTest = "testuser";
    private String usernameTest2 = "testuser2";
    private String passwordTest = "password123";
    private String emailTest = "test@example.com";
    private String roleCode = "USER";
    private String roleType = "REGULAR_USER";
    
    private String adminUsername = "testuser1";
    private String updatedPassword = "updatedpassword";
    private String updatedEmail = "updated@example.com";
    private String nonexistentUsername = "nonexistentuser";
    private String nonAdminUsername = "testuser2";

    private String newUsername = "newuser";
    private String newPassword = "newpassword";
    private String newEmail = "new@example.com";

    @BeforeEach
    void cleanDatabase() {
        // entityManager.createNativeQuery("DELETE FROM role").executeUpdate();
        // entityManager.clear();
        // entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        // entityManager.clear();

        User user = new User();
        user.setUsername(usernameTest);
        user.setPassword(passwordTest);
        user.setEmail(emailTest);        

        Role role = new Role();        
        role.setRolecode(roleCode);   
        role.setRoletype(roleType);
        role.setUser(user);

        entityManager.persist(user);
        entityManager.persist(role);
        entityManager.flush();
    }

    @Test
    void getAllUsers_WhenNormal_ShouldReturnListOfUsers() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                                .header("X-Current-User", adminUsername))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        List<UserDTO> actualUsers = objectMapper.readValue(responseBody, new TypeReference<List<UserDTO>>() {});
        
        assertEquals(actualUsers.size(), 3 );
    }

    @Test
    void getUserByUsername_WhenExist_ShouldReturnUser() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{username}", usernameTest))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        UserDTO actualUser = objectMapper.readValue(responseBody, UserDTO.class);

        assertEquals(actualUser.getUsername(), usernameTest);
        assertEquals(actualUser.getPassword(), passwordTest);
        assertEquals(actualUser.getEmail(), emailTest);
    }

    @Test
    void getUserByUsername_WhenNotExist_ShouldReturnNotFound() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{username}", "nonexistentuser"))
                                .andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("User not found"));
    }

    @Test
    void updateUser_WhenExist_ShouldReturnUpdatedUser() throws Exception {
        UserDTO updatedUser = new UserDTO();
        updatedUser.setUsername(usernameTest);
        updatedUser.setPassword(updatedPassword);
        updatedUser.setEmail(updatedEmail);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{username}", usernameTest)
                                    .header("X-Current-User", adminUsername)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updatedUser)))
                                    .andExpect(MockMvcResultMatchers.status().isOk())
                                    .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UserDTO actualUser = objectMapper.readValue(responseBody, UserDTO.class);

        assertEquals(actualUser.getUsername(), usernameTest);
        assertEquals(actualUser.getEmail(), updatedEmail);
        assertNotEquals(updatedPassword, actualUser.getPassword());
        assertTrue(actualUser.getPassword().startsWith("$2a$"));
    }

    @Test
    void updateUser_WhenNotExist_ShouldReturnNotFound() throws Exception {
        UserDTO updatedUser = new UserDTO();
        updatedUser.setUsername(nonexistentUsername);
        updatedUser.setPassword(updatedPassword);
        updatedUser.setEmail(updatedEmail);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{username}", nonexistentUsername)
                                    .header("X-Current-User", adminUsername)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updatedUser)))
                                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                                    .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("User not found with username: " + nonexistentUsername));
    }   

    @Test
    void updateUser_WhenNotAdminAndNotTargetUser_ShouldReturnForbidden() throws Exception {
        UserDTO updatedUser = new UserDTO();
        updatedUser.setUsername(usernameTest);
        updatedUser.setPassword(updatedPassword);
        updatedUser.setEmail(updatedEmail);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{username}", usernameTest2)
                                    .header("X-Current-User", nonAdminUsername)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updatedUser)))
                                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                    .andReturn();

        // String responseBody = result.getResponse().getContentAsString();
        // assertTrue(responseBody.contains("You don't have permission to edit this user"));
    }

    @Test
    void createUser_WhenNormal_ShouldReturnCreatedUser() throws Exception {
        UserDTO newUser = new UserDTO();
        newUser.setUsername(newUsername);
        newUser.setPassword(newPassword);
        newUser.setEmail(newEmail);
        
        // Add a role to the new user
        RoleDTO role = new RoleDTO();
        role.setUsername(newUsername);
        role.setRolecode("USER");
        role.setRoletype("REGULAR_USER");
        newUser.setRoles(Arrays.asList(role));
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/")
                                    .header("X-Current-User", adminUsername)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(newUser)))
                                    .andExpect(MockMvcResultMatchers.status().isOk())
                                    .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        UserDTO actualUser = objectMapper.readValue(responseBody, UserDTO.class);

        assertEquals(actualUser.getUsername(), newUsername);
        assertTrue(actualUser.getPassword().startsWith("$2a$"));
        assertEquals(actualUser.getEmail(), newEmail);
        assertNotNull(actualUser.getRoles());
        assertFalse(actualUser.getRoles().isEmpty());
        assertEquals("USER", actualUser.getRoles().get(0).getRolecode());
    }    
    
    @Test
    void createUser_WhenNormal_ShouldReturnCreatedUser_SecondTime() throws Exception {
        UserDTO newUser = new UserDTO();
        newUser.setUsername(newUsername);
        newUser.setPassword(newPassword);
        newUser.setEmail(newEmail);
        
        // Add a role to the new user
        RoleDTO role = new RoleDTO();
        role.setUsername(newUsername);
        role.setRolecode("USER");
        role.setRoletype("REGULAR_USER");
        newUser.setRoles(Arrays.asList(role));
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/")
                                    .header("X-Current-User", adminUsername)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(newUser)))
                                    .andExpect(MockMvcResultMatchers.status().isOk())
                                    .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        UserDTO actualUser = objectMapper.readValue(responseBody, UserDTO.class);

        assertEquals(actualUser.getUsername(), newUsername);
        assertTrue(actualUser.getPassword().startsWith("$2a$"));
        assertEquals(actualUser.getEmail(), newEmail);
        assertNotNull(actualUser.getRoles());
        assertFalse(actualUser.getRoles().isEmpty());
        assertEquals("USER", actualUser.getRoles().get(0).getRolecode());
    } 

}
