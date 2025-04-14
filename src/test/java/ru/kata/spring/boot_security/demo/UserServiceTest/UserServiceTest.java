package ru.kata.spring.boot_security.demo.UserServiceTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password123");

        userService.saveUser(user);

        // Проверяем, что пароль сохранен в хэшированном виде
        assertNotEquals("password123", user.getPassword());
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));
    }
}
