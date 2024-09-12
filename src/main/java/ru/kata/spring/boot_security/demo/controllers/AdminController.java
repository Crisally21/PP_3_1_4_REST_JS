package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Services.RegistrationService;
import ru.kata.spring.boot_security.demo.Services.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.Services.UserService;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.util.UserValidate;

import javax.validation.Valid;
import java.util.Collections;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final UserValidate userValidate;
    private final RegistrationService registrationService;

    @Autowired
    public AdminController(UserService userService, UserValidate userValidate, RegistrationService registrationService) {
        this.userService = userService;
        this.userValidate = userValidate;
        this.registrationService = registrationService;
    }

    @GetMapping()
    public String usersPage(Model model) {
        model.addAttribute("listUser", userService.index());
        model.addAttribute("thisUser", userService.findByName(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        return "admin/users";
    }


    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("thisUser", userService.findByName(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        return "admin/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult) {
        userValidate.validate(user, bindingResult);

        if (bindingResult.hasErrors())
            return "admin/new";

        registrationService.register(user);
        return "redirect:admin";
    }
}