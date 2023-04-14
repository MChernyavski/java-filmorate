package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public Map<Long, User> users() {
        return users;
    }

    @Override
    public User addUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Добавили пользователя: {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("ERROR: Не существует пользователя с таким id {} ", user.getId());
            throw new NotFoundException("Отсутствует пользователь c id " + user.getId());
        }
        users.put(user.getId(), user);
        log.info("Обновили пользователя: {}", user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получили список пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            log.error("ERROR: Не существует пользователя с таким id {} ", id);
            throw new NotFoundException("Отсутствует пользователь c id " + id);
        }
        return users.get(id);
    }
}
