package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Services.RegistrationService;
import ru.kata.spring.boot_security.demo.Services.RoleService;
import ru.kata.spring.boot_security.demo.Services.UserService;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.util.UserValidate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/rest/admin")
public class AdminRestController {

    private UserService userService;
    private RoleService roleService;
    private RegistrationService registrationService;
    private UserValidate userValidate;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService, RegistrationService registrationService, UserValidate userValidate) {
        this.userService = userService;
        this.roleService = roleService;
        this.registrationService = registrationService;
        this.userValidate = userValidate;
    }

    @PostMapping
    public ResponseEntity<List<User>> loadUsers() {
        return new ResponseEntity<>(userService.index(), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
    }

    @PutMapping("/edit/{id}")
    public void updateUser(@PathVariable("id") int id, @RequestBody User updatedUser) {
        if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
            updatedUser.setPassword(userService.show(id).getPassword());
        } else {
            updatedUser.setPassword(registrationService.encodePassword(updatedUser.getPassword()));
        }

        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            String roleName = updatedUser.getRoles().iterator().next().getName();
            Role newRole = new Role("ROLE_" + roleName);
            updatedUser.setRoles(Collections.singleton(newRole));
            roleService.save(newRole);
        }

        userService.update(id, updatedUser);
    }

    @PostMapping("/new")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user,
                                        BindingResult bindingResult) {
        userValidate.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        registrationService.register(user);
        return ResponseEntity.ok().build();
    }

}
