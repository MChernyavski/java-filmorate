package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addToFriend(long userId, long friendId) {
        try { String sqlToFriend = "insert into friendship (user_id, friend_id, status) values (?, ?, false)";
        jdbcTemplate.update(sqlToFriend, userId, friendId);
    } catch (DataAccessException e) {
            throw new ValidateException(String.format("Пользователь с id = %s уже в друзьях у пользователя с id = %s",
                    friendId, userId));
        }
    }

    @Override
    public void deleteFromFriend(long userId, long friendId) {
        String sqlFromFriend = "delete from friendship where user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlFromFriend, userId, friendId);
    }

    @Override
  public void updateFriendStatus(long userId, long friendId, boolean status) {
            String sqlUpdateStatus = "update friendship set status = ? where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlUpdateStatus, status, userId, friendId);
    }

    @Override
    public List<Long> getAllFriendsByUser(long id) {
        String sqlAllFriends = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ? AND STATUS = TRUE UNION SELECT " +
                "USER_ID FROM FRIENDSHIP WHERE FRIEND_ID = ?";
        return jdbcTemplate.query(sqlAllFriends, (rs, rowNum) -> rs.getLong("friend_id"), id, id);
    }

}
