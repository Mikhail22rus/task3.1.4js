package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping
    public String userList(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("principal", user);
        model.addAttribute("newUser", new User());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin"; // твой шаблон admin.html
    }


    @PostMapping
    public String userCreate(@ModelAttribute("newUser") User user,
                             @RequestParam("roleIds") List<Long> roleIds) {
        List<Role> roles = new ArrayList<>();
        for (Long roleId : roleIds) {
            Role role = roleService.getRoleById(roleId);
            roles.add(role);
        }
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin";
    }


    @PostMapping("/update/{id}")
    public String userEdit(@PathVariable("id") Long id,
                           @ModelAttribute("user") User updatedUser,
                           @RequestParam("roleIds") List<Long> roleIds) {
        User existingUser = userService.getUserById(id);
        if (existingUser != null) {
            updatedUser.setId(id);
            List<Role> roles = new ArrayList<>();
            for (Long roleId : roleIds) {
                Role role = roleService.getRoleById(roleId);
                roles.add(role);
            }
            updatedUser.setRoles(roles);
            userService.saveUser(updatedUser);
        }
        return "redirect:/admin";
    }


    @PostMapping("/delete/{id}")
    public String userDelete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
