package com.example.demo.security;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.TableGenerator;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDetailServiceImplTests {

    private UserDetailsServiceImpl userDetailsService;

    private UserRepository userRepository = mock(UserRepository.class);


    @Before
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl();
        TestUtils.injectObjects(userDetailsService, "userRepository", userRepository);
        User user = new User();
        user.setUsername("TestUser");
        user.setPassword("testPassword");
        when(userRepository.findByUsername("TestUser")).thenReturn(user);

    }

    @Test
    public void loadUserByUsername_HappyPath() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("TestUser");
        assertNotNull(userDetails);
        Collection<? extends GrantedAuthority> authorityCollection = userDetails.getAuthorities();
        assertNotNull(authorityCollection);
        assertEquals("TestUser", userDetails.getUsername());
        assertEquals("testPassword", userDetails.getPassword());
    }
}
