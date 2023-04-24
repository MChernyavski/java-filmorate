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
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addToFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFromFriend(id, friendId);
    }

    @GetMapping("/{id}/friends") //возвращаем список пользователей-друзей
    public List<User> getAllFriends(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
