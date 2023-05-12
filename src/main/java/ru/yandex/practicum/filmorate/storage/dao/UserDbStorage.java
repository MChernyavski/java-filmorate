package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Primary
@Repository("UserDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlUpdateUser = "update users set email = ?, name = ?, login = ?, birthday = ? where user_id = ?";
        if (jdbcTemplate.update(sqlUpdateUser, user.getEmail(), user.getLogin(), user.getName(), user.getId()) > 0) {
            return user;
        }
        log.error("ERROR: Не существует пользователя с таким id {} ", user.getId());
        throw new NotFoundException("Отсутствует пользователь c id " + user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        String sqlAllUsers = "select * from users";
        return jdbcTemplate.query(sqlAllUsers, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public User getUserById(long id) {
        String sqlUserById = "select * from users where user_id = ?";
        try {
            return (User) jdbcTemplate.query(sqlUserById, (rs, rowNum) -> mapRowToUser(rs), id);
        } catch (NotFoundException e) {
            log.error("ERROR: Не существует пользователя с таким id {} ", id);
            throw new NotFoundException("Отсутствует пользователь c id " + id);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return User.builder()
                .id(id)
                .email(email)
                .name(name)
                .login(login)
                .birthday(birthday)
                .build();
    }
}
