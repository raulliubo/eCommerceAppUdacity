package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTests {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);


    @Before
    public void setUp() {
        userController = new UserController();

        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

        Cart cart = new Cart();
        cart.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        user.setPassword("testPassword");
        user.setCart(cart);
        cart.setUser(user);
        cart.setTotal(new BigDecimal(9.99));
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
    }

    @Test
    public void createUser_HappyPath() {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("TestUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals("TestUser", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void createUser_PasswordMismatch() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("TestUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword1");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUser_PasswordTooShort() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("TestUser");
        createUserRequest.setPassword("1234");
        createUserRequest.setConfirmPassword("1234");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findById_HappyPath() {
        ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals("TestUser", user.getUsername());
    }

    @Test
    public void findById_IdError() {
        ResponseEntity<User> response = userController.findById(5L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void findByName_HappyPath() {
        ResponseEntity<User> response = userController.findByUserName("TestUser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals("TestUser", user.getUsername());
    }

    @Test
    public void findByName_NameError() {
        ResponseEntity<User> response = userController.findByUserName("TestUser_Wrong");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
