package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {

    private static UserController userController;
    private static User user1;
    private static User user2;

    @BeforeAll
    public static void init() {
        userController = new UserController(new InMemoryUserService());
    }

    @BeforeEach
    public void beforeEach() {
        user1 = User.builder()
                .email("email1@email.ru")
                .login("login1")
                .birthday(LocalDate.of(1991, 7, 16))
                .name("name1")
                .build();

        user2 = User.builder()
                .email("email2@email.ru")
                .login("login2")
                .birthday(LocalDate.of(1997, 3, 12))
                .name("name2")
                .build();
    }

    @Test
    public void addUserTest() {
        userController.addUser(user1);
        userController.addUser(user2);
        List<User> getUsers = userController.getAll();
        assertEquals(2, getUsers.size());
        assertNotNull(getUsers);
    }

    @Test
    public void updateUserTest() {
        User actualUser = userController.addUser(user1);
        actualUser.setId(user1.getId());
        actualUser.setName("John");
        actualUser.setLogin("Jo12");
        User newUser = userController.updateUser(actualUser);
        assertEquals(actualUser, newUser);
        assertNotNull(newUser);
    }

    @Test
    public void addUserWithEmptyEmailTest() {
        user1.setEmail(" ");
        ValidateException exception = assertThrows(ValidateException.class, () -> userController.addUser(user1));
        String newErrorMessage = "Электронная почта не может быть пустой";
        assertEquals(newErrorMessage, exception.getMessage());
    }

    @Test
    public void addUserWithEmailWithoutAtTest() {
        user1.setEmail("email1.email.ru");
        ValidateException exception = assertThrows(ValidateException.class, () -> userController.addUser(user1));
        String newErrorMessage = "Электронная почта должна содержать символ @";
        assertEquals(newErrorMessage, exception.getMessage());
    }

    @Test
    public void addUserWithEmptyLoginTest() {
        user1.setLogin("");
        ValidateException exception = assertThrows(ValidateException.class, () -> userController.addUser(user1));
        String newErrorMessage = "Логин не может быть пустым";
        assertEquals(newErrorMessage, exception.getMessage());
    }

    @Test
    public void addUserWithLoginBlankTest() {
        user1.setLogin("login 1");
        ValidateException exception = assertThrows(ValidateException.class, () -> userController.addUser(user1));
        String newErrorMessage = "Логин не может содержать пробелы";
        assertEquals(newErrorMessage, exception.getMessage());
    }

    @Test
    public void addUserWithBirthdayInFuture() {
        user1.setBirthday(LocalDate.of(2055, 12, 14));
        ValidateException exception = assertThrows(ValidateException.class, () -> userController.addUser(user1));
        String newErrorMessage = "Дата рождения не может быть в будущем";
        assertEquals(newErrorMessage, exception.getMessage());
    }
}
