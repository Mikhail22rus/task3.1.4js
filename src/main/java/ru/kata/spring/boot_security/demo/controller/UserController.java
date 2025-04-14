package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    private final RoleServiceImpl roleService;

    @Autowired
    public UserController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String userHomePage(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", currentUser);
        return "userPage";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "createUser";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String createUser(@ModelAttribute("user") User user, @RequestParam("roles") List<Long> roleIds) {
        List<Role> userRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            Role role = roleService.getRoleById(roleId);
            userRoles.add(role);
        }
        user.setRoles(userRoles);
        userService.saveUser(user);

        return "redirect:/users/admin";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/update")
    public String updateUser(@RequestParam("id") Long id, @ModelAttribute("user") User updatedUser) {
        updatedUser.setId(id);
        userService.saveUser(updatedUser);
        return "redirect:/users/admin";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/users/admin";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/user/info")
    @PreAuthorize(" hasRole('ROLE_ADMIN')")
    public String getUserInfo(@RequestParam Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "userPage";
    }
}

