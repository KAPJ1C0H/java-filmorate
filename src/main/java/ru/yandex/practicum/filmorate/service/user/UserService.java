package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendDbStorage;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private UserStorage userStorage;
    private FriendDbStorage friendDbStorage;

    public Collection<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public User addFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new DuplicatedDataException("Id пользователей не должны совпадать");
        }
        friendDbStorage.addFriend(userId, friendId);
        return userStorage.findById(userId);
    }

    public User deleteFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new DuplicatedDataException("Id пользователей не должны совпадать");
        }
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        if (user.getFriends().contains(friend.getId())) {
            friendDbStorage.deleteFriend(userId, friendId);
        }
        return userStorage.findById(userId);
    }

    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        return userStorage.getCommonFriends(firstUserId, secondUserId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User delete(Long id) {
        return userStorage.delete(id);
    }
}
