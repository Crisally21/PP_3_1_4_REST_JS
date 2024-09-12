package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Services.RegistrationService;
import ru.kata.spring.boot_security.demo.Services.RoleService;
import ru.kata.spring.boot_security.demo.Services.UserService;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/rest/admin")
public class AdminRestController {

    private UserService userService;
    private RoleService roleService;
    private RegistrationService registrationService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService, RegistrationService registrationService) {
        this.userService = userService;
        this.roleService = roleService;
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<List<User>> loadUsers() {
        return new ResponseEntity<>(userService.index(), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam("id") int id) {
        userService.delete(id);
    }

    @PutMapping("/edit")
    public void updateUser(@RequestParam(value = "id") int id, String name, String password, String email, int age, String role) {
        User user = new User();
        user.setPassword(password.length() < 3 ? userService.show(id).getPassword() : registrationService.encodePassword(password));
        user.setName(name);
        user.setAge(age);
        user.setEmail(email);
        Role newRole = new Role("ROLE_" + role);
        user.setRoles(Collections.singleton(newRole));
        roleService.save(newRole);
        userService.update(id, user);
    }

}
