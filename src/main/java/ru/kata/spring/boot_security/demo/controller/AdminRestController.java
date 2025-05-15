package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("")
    public ResponseEntity<List<User>> userList() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<User> userGet(@PathVariable("id") Long id) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(existingUser, HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/users")
    public ResponseEntity<User> userCreate(@RequestBody User user, @RequestParam List<Long> roleIds) {
        List<Role> roles = roleService.getRolesByIds(roleIds);
        user.setRoles(roles);
        userService.saveUser(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<User> userEdit(@PathVariable("id") Long id, @RequestBody User updatedUser, @RequestParam List<Long> roleIds) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedUser.setId(id);

        if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
            updatedUser.setPassword(existingUser.getPassword());
        }
        List<Role> roles = roleService.getRolesByIds(roleIds);
        updatedUser.setRoles(roles);

        userService.saveUser(updatedUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> userDelete(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
