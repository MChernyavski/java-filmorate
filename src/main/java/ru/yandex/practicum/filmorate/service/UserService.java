package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public User addToFriend(long userId, long friendId) {
        if (!userStorage.users().containsKey(userId)) {
            log.error("ERROR: Не существует пользователя с таким id {} ", userId);
            throw new NotFoundException("Отсутствует пользователь c id " + userId);
        }
        if (!userStorage.users().containsKey(friendId)) {
            log.error("ERROR: Не существует пользователя с таким id {} ", friendId);
            throw new NotFoundException("Отсутствует пользователь c id " + friendId);
        }
        if (getUserById(userId).getFriends().contains(friendId)) {
            log.error("ERROR: Пользователь с id {} уже в друзьях у пользователя с id {} ", userId, friendId);
            throw new ValidateException("Пользователь с id " + userId + " уже в друзьях у пользователя с id " + friendId);
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Пользователь с id {} добавлен в друзья к {}", friendId, userId);
        log.info("Пользователь с id {} добавлен в друзья к {}", userId, friendId);

        return getUserById(userId);
    }

    public User deleteFromFriend(long userId, long friendId) {
        if (!userStorage.users().containsKey(userId)) {
            log.error("ERROR: Не существует пользователя с таким id {} ", userId);
            throw new NotFoundException("Отсутствует пользователь c id " + userId);
        }
        if (!userStorage.users().containsKey(friendId)) {
            log.error("ERROR: Не существует пользователя с таким id {} ", friendId);
            throw new NotFoundException("Отсутствует пользователь c id " + friendId);
        }
        if (!userStorage.users().get(userId).getFriends().contains(friendId)) {
            log.error("ERROR: Пользователя с id {} нет в друзьях у пользователя с id {} ", userId, friendId);
            throw new NotFoundException("Пользователя с id " + userId + " нет в друзьях у пользователя с id " + friendId);
        }
        getUserById(userId).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(userId);
        log.info("Пользователь с id {} удалён из друзей {}", friendId, userId);
        log.info("Пользователь с id {} удалён из друзей {}", userId, friendId);
        return getUserById(userId);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        if (!userStorage.users().containsKey(userId)) {
            log.error("ERROR: Не существует пользователя с таким id {} ", userId);
            throw new NotFoundException("Отсутствует пользователь c id " + userId);
        }
        if (!userStorage.users().containsKey(friendId)) {
            log.error("ERROR: Не существует пользователя с таким id {} ", friendId);
            throw new NotFoundException("Отсутствует пользователь c id " + friendId);
        }
        return getUserById(userId).getFriends()
                .stream()
                .filter(getUserById(friendId).getFriends()::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getAllFriends(long id) {
        return getUserById(id).getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
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
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("ERROR: дата рождения не может быть в будущем");
            throw new ValidateException("Дата рождения не может быть в будущем");
        }
    }
}

