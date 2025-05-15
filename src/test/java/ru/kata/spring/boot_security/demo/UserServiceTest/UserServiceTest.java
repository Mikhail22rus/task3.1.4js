package ru.kata.spring.boot_security.demo.UserServiceTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setName("Test Name");
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        try {
            userService.saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        User savedUser = userService.findByEmail("test@example.com");

        assertNotEquals("password123", savedUser.getPassword());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }
}
