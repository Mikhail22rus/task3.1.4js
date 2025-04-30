package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();

    Role saveRole(Role role);

    Role getRoleById(Long id);

    List<Role> getRolesByIds(List<Long> roleIds);
}
