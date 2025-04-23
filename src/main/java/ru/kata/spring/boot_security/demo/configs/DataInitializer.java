package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public DataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = new Role();
        adminRole.setRole("ROLE_ADMIN");

        Role userRole = new Role();
        userRole.setRole("ROLE_USER");

        roleService.saveRole(adminRole);
        roleService.saveRole(userRole);

        User admin = new User("Иван Иванов", "admin", "admin", "admin@example.com", Collections.singletonList(adminRole));
        User user = new User("Петр Петров", "user", "user", "user@example.com", Collections.singletonList(userRole));
        admin.setAge(32);
        user.setAge(45);

        userService.saveUser(admin);
        userService.saveUser(user);
    }
}
