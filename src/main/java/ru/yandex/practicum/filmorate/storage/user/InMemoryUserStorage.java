package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Override
    public User create(User user) {
        isEmailExists(user);
        user.setId(getUserId());
        users.put(user.getId(), user);
        log.info("Новый пользователь: {} Общее количество пользователей: {}", user, users.size());
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Старые данные о пользователе: {}", users.get(user.getId()));
        User newUser = users.computeIfPresent(user.getId(), (key, value) -> value = user);
        if (newUser != null) {
            log.info("Новые данные о пользователе: {}", users.get(user.getId()));
            return newUser;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    @Override
    public User delete(Long id) {
        return Optional.ofNullable(users.remove(id))
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Override
    public Collection<User> getFriends(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user1 = users.get(userId);
        User user2 = users.get(otherId);

        if (user1 == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (user2 == null) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }

        Set<Long> friends1 = user1.getFriends();
        Set<Long> friends2 = user2.getFriends();

        return friends1.stream()
                .filter(friends2::contains)
                .map(this::findById)
                .collect(Collectors.toList());
    }

    private long getUserId() {
        return ++userId;
    }

    private void resetUserId() {
        userId = 0;
    }

    private void isEmailExists(User newUser) {
        boolean result = users.values()
                .stream()
                .filter(user -> !(user.getId() == newUser.getId()))
                .map(User::getEmail)
                .anyMatch(email -> email.equals(newUser.getEmail()));
        if (result) {
            String message = "Этот имейл уже используется";
            log.warn(message);
            throw new DuplicatedDataException(message);
        }
    }
}