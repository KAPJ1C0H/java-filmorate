package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class FriendDbStorage extends BaseRepository<User> {
    private static final String ADD_QUERY = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_ALL_USER_FRIENDS_QUERY = "DELETE FROM user_friends WHERE user_id = ? " +
            "OR friend_id = ?";
    private static final String FIND_FRIENDS_BY_ID_QUERY = "SELECT friend_id FROM user_friends WHERE user_id = ?";
    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT * FROM user_friends";

    public FriendDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    public void addFriend(Long userId, Long friendId) {
        try {
            update(ADD_QUERY, userId, friendId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        try {
            update(DELETE_QUERY, userId, friendId);
        } catch (InternalServerException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public Set<Long> getFriendsId(Long userId) {
        List<Long> friends = jdbc.queryForList(FIND_FRIENDS_BY_ID_QUERY, Long.TYPE, userId);
        return new HashSet<>(friends);
    }

    public Map<Long, Set<Long>> getAllFriendsId() {
        Map<Long, Set<Long>> friends = new HashMap<>();
        return jdbc.query(FIND_ALL_FRIENDS_QUERY, (ResultSet resultSet) -> {
            while (resultSet.next()) {
                Long userId = resultSet.getLong("user_id");
                Long friendId = resultSet.getLong("friend_id");
                friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
            }
            return friends;
        });
    }

    public void deleteAllUserFriends(Long userId) {
        update(DELETE_ALL_USER_FRIENDS_QUERY, userId, userId);
    }
}
