package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;

@Repository("FriendshipDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addToFriend(long userId, long friendId) {
        String sqlToFriend = "insert into friendship (user_id, friend_id, status) values (?, ?, false)";
        jdbcTemplate.update(sqlToFriend, userId, friendId);
    }

    @Override
    public void deleteFromFriend(long userId, long friendId) {
        String sqlFromFriend = "delete from friendship where (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sqlFromFriend, userId, friendId);
    }

    @Override
    public void updateFriendStatus(long userId, long friendId, boolean status) {
        String sqlUpdateStatus = "update friendship set status = ? where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlUpdateStatus, userId, friendId, status);
    }

    /*@Override
    public List<Long> getCommonFriends(long userId, long friendId) {
        String sqlCommon = "select u.* from users as u inner join friendship as fr on u.user_id = fr.friend_id " +
                "where user_id = ? and friend_id in (select friend_id from friendship where user_id = ?)";
        return jdbcTemplate.queryForList(sqlCommon, long.class, userId, friendId);
    }
     */

    @Override
    public List<Long> getAllFriends(long id) {
        String sqlAllFriends = "select user_id, name from users where user_id IN " +
                "(select friend_id from friendship where user_id = ? and status='true')";
        return jdbcTemplate.queryForList(sqlAllFriends, long.class, id);
    }

}