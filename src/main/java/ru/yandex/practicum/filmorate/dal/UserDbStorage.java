package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    FriendDbStorage friendDbStorage;
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, " +
            "name = ?, birthday = ? WHERE id = ?";
    private static final String FIND_MANY_QUERY = "SELECT * FROM users WHERE id IN (%s)";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT id FROM users WHERE id IN " +
            "(SELECT friend_id FROM user_friends WHERE user_id = ?) AND id IN " +
            "(SELECT friend_id FROM user_friends WHERE user_id = ?);";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, FriendDbStorage friendDbStorage) {
        super(jdbc, mapper, User.class);
        this.friendDbStorage = friendDbStorage;
    }

    @Override
    public Collection<User> getFriends(Long id) {
        findById(id);
        Collection<Long> friends = friendDbStorage.getFriendsId(id);
        return findMany(friends);
    }

    @Override
    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        Collection<Long> friends = jdbc.queryForList(FIND_COMMON_FRIENDS_QUERY, Long.TYPE, firstUserId, secondUserId);
        return findMany(friends);
    }

    @Override
    public Collection<User> findAll() {
        Collection<User> users = findMany(FIND_ALL_QUERY);
        Map<Long, Set<Long>> friends = friendDbStorage.getAllFriendsId();
        for (User user : users) {
            user.setFriends(friends.getOrDefault(user.getId(), new HashSet<>()));
        }
        return users;
    }

    @Override
    public User findById(Long id) {
        User user = findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
        user.setFriends(friendDbStorage.getFriendsId(user.getId()));
        return user;
    }

    @Override
    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay())
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        try {
            update(
                    UPDATE_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Timestamp.valueOf(user.getBirthday().atStartOfDay()),
                    user.getId()
            );
            return findById(user.getId());
        } catch (InternalServerException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public User delete(Long id) {
        User user = findById(id);
        if (!user.getFriends().isEmpty()) friendDbStorage.deleteAllUserFriends(id);
        update(DELETE_QUERY, id);
        return user;
    }

    private Collection<User> findMany(Collection<Long> usersId) {
        if (usersId.isEmpty()) return new ArrayList<>();
        String inSql = String.join(",", Collections.nCopies(usersId.size(), "?"));

        List<User> result = jdbc.query(
                String.format(FIND_MANY_QUERY, inSql),
                usersId.toArray(),
                mapper
        );
        if (result.size() != usersId.size()) throw new ValidationException("Указан User с неверным id");
        return result;
    }
}
