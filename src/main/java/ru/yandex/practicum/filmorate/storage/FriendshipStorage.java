package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import java.util.List;

public interface FriendshipStorage {
    void addToFriend(long userId, long friendId);
    void deleteFromFriend(long userId, long friendId);
    void updateFriendStatus(long userId, long friendId, boolean status);
   // List<Long> getCommonFriends(long userId, long friendId);
    List<Long> getAllFriends(long id);
}
