package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);


    void deleteUser(Long id);


    User getUserById(Long id);


    List<User> getAllUsers();


    User findByUsername(String username);

    User findByEmail(String email);
}
