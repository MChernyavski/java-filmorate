package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("UserDbStorage") UserStorage userStorage, FriendshipDbStorage friendshipDbStorage) {
        this.userStorage = userStorage;
        this.friendshipDbStorage = friendshipDbStorage;
    }

    @Override
    public User addUser(User user) {
        validateUser(user);
        userStorage.addUser(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        userStorage.updateUser(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public void addToFriend(long userId, long friendId) {
        if (!friendshipDbStorage.getAllFriends(userId).contains(friendId) &&
                !friendshipDbStorage.getAllFriends(friendId).contains(userId)) {
            friendshipDbStorage.addToFriend(userId, friendId);
            log.info("Пользователь с id {} добавлен в друзья к {}", friendId, userId);
            log.info("Пользователь с id {} добавлен в друзья к {}", userId, friendId);
        } else if (friendshipDbStorage.getAllFriends(userId).contains(friendId) &&
                friendshipDbStorage.getAllFriends(friendId).contains(userId)) {
            friendshipDbStorage.updateFriendStatus(userId, friendId, true);
        } else {
            log.error("ERROR: Пользователь с id {} уже в друзьях у пользователя с id {} ", userId, friendId);
        }
    }

    @Override
    public void deleteFromFriend(long userId, long friendId) { // изменить
        getUserById(userId);
        getUserById(friendId);
        if (!friendshipDbStorage.getAllFriends(userId).contains(friendId)) {
            log.error("ERROR: Пользователя с id {} нет в друзьях у пользователя с id {} ", friendId, userId);
            throw new NotFoundException("Пользователя с id " + friendId + " нет в друзьях у пользователя с id " + userId);
        } else if (!friendshipDbStorage.getAllFriends(friendId).contains(userId)) {
            log.error("ERROR: Пользователя с id {} нет в друзьях у пользователя с id {} ", userId, friendId);
            throw new NotFoundException("Пользователя с id " + userId + " нет в друзьях у пользователя с id " + friendId);
        } else {
            friendshipDbStorage.deleteFromFriend(friendId, userId);
            friendshipDbStorage.updateFriendStatus(userId, friendId, false);
        }
    }

        @Override
        public List<User> getCommonFriends(long userId, long friendId) {
            getUserById(userId);
            getUserById(friendId);
            return friendshipDbStorage.getAllFriends(userId)
                    .stream()
                    .filter(friendshipDbStorage.getAllFriends(friendId)::contains)
                    .map(this::getUserById)
                    .collect(Collectors.toList());
        }
        @Override
        public List<User> getAllFriends (long id) {
            return friendshipDbStorage.getAllFriends(id)
                    .stream()
                    .map(this::getUserById)
                    .collect(Collectors.toList());
        }

        public void validateUser (User user){
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

