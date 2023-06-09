package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDbStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, FriendshipDbStorage friendshipDbStorage) {
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
    public User getUserById(long userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public List<User> getUsersByIds(List<Long> ids) {
        return userStorage.getAllById(ids);
    }

    @Override
    public void addToFriend(long friendId, long userId) {
        Set<Long> usersFriends = getUserById(userId).getFriends();
        Set<Long> friendsFriends = getUserById(friendId).getFriends();
        boolean isUserHasFriend = usersFriends.contains(friendId);
        boolean isFriendHasUserFriend = friendsFriends.contains(userId);
        if (!isUserHasFriend && !isFriendHasUserFriend) {
            friendshipDbStorage.addToFriend(userId, friendId);
            usersFriends.add(friendId);
            log.info("Пользователь с id {} добавлен в друзья к {}", userId, friendId);
        } else if (!isUserHasFriend) {
            friendshipDbStorage.addToFriend(userId, friendId);
            friendshipDbStorage.updateFriendStatus(userId, friendId, true);
            friendshipDbStorage.updateFriendStatus(friendId, userId, true);
            log.info("Пользователь id = {} подтвердил дружбу с пользователем id = {}", userId, friendId);
            usersFriends.add(friendId);
        } else {
            log.info("Пользователь id = {} уже в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException(format("Пользователь id = %s уже в друзьях у пользователя id = %s",
                    friendId, userId));
        }
    }

    @Override
    public void deleteFromFriend(long friendId, long userId) {
        Set<Long> usersFriends = getUserById(userId).getFriends();
        Set<Long> friendsFriends = getUserById(friendId).getFriends();
        if (!friendsFriends.contains(userId)) {
            friendshipDbStorage.deleteFromFriend(userId, friendId);
            log.info("Пользователь id = {} удалил из друзей пользователя id = {}", userId, friendId);
        } else if (!usersFriends.contains(friendId)) {
            log.error("Пользователь id = {} не в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException(format("Пользователь id = %s не в друзьях у пользователя id = %s",
                    friendId, userId));
        } else {
            friendshipDbStorage.deleteFromFriend(userId, friendId);
            friendshipDbStorage.updateFriendStatus(friendId, userId, false);
            log.info("Пользователь id = {} удалил из друзей пользователя id = {}, статус дружбы обновлен",
                    userId, friendId);
        }
    }

    @Override
    public List<User> getAllFriends(long id) {
        return getUsersByIds(friendshipDbStorage.getAllFriendsByUser(id));
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        Collection<Long> userFriendsIds = friendshipDbStorage.getAllFriendsByUser(userId);
        List<Long> commonFriendsIds = friendshipDbStorage.getAllFriendsByUser(friendId)
                .stream()
                .filter(userFriendsIds::contains)
                .collect(Collectors.toList());
        return userStorage.getAllById(commonFriendsIds);
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

