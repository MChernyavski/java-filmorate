package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Добавили пользователя: {}", user.getId());
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновили пользователя: {}", user.getId());
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Получили список пользователей");
        return userService.getAllUsers();
    }
}
