package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InMemoryUserService implements UserService {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public User addUser(User user) {
        validateUser(user);
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("ERROR: Не существует пользователя с таким id {} ", user.getId());
            throw new ValidateException("Отсутствует пользователь c id " + user.getId());
        } else {
            validateUser(user);
            users.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            log.error("ERROR: электронная почта пустая");
            throw new ValidateException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.error("ERROR: в электронном почте нет символа @");
            throw new ValidateException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            log.error("ERROR: логин пустой");
            throw new ValidateException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.error("ERROR: логин содержит пробелы");
            throw new ValidateException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            log.error("ERROR: Поле Name пустой. Используйте логин в качестве имени {} ", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("ERROR: дата рождения не может быть в будущем");
            throw new ValidateException("Дата рождения не может быть в будущем");
        }
    }
}

