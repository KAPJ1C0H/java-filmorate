package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        isEmailExists(user);
        user.setId(getUserId());
        users.put(user.getId(), user);
        log.info("Новый пользователь: {} Общее количество пользователей: {}", user, users.size());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        isEmailExists(newUser);
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser = newUser;
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private Long getUserId() {
        return ++userId;
    }

    private void resetUserId() {
        userId = 0L;
    }

    private void isEmailExists(User newUser) {
        boolean result = users.values()
                .stream()
                .filter(user -> !(user.getId().equals(newUser.getId())))
                .map(User::getEmail)
                .anyMatch(email -> email.equals(newUser.getEmail()));
        if (result) {
            String message = "Этот имейл уже используется";
            log.warn(message);
            throw new DuplicatedDataException(message);
        }
    }
}
