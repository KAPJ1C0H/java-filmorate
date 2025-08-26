package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserTest {
    @Autowired
    private Validator validator;

    private User user;
    private Set<ConstraintViolation<User>> violations;

    @BeforeEach
    void setUp() {
        user = new User(1L, "ivan@yandex.ru", "Ivan123", "Ivan",
                LocalDate.of(1996, 12, 3));
        violations = validator.validate(user);
    }

    @Test
    void emailValidationTest() {
        assertTrue(violations.isEmpty());

        user.setEmail(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());

        user.setEmail(" ");
        violations = validator.validate(user);
        assertEquals(2, violations.size());

        user.setEmail("ivan&yandex.ru");
        violations = validator.validate(user);
        assertEquals(1, violations.size());

        user.setEmail(" ivan@yandex.ru");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void loginValidationTest() {
        assertTrue(violations.isEmpty());

        user.setLogin(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());

        user.setLogin(" ");
        violations = validator.validate(user);
        assertEquals(2, violations.size());

        user.setLogin("Ivan 123");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void nameValidationTest() {
        user.setName(null);
        assertEquals(user.getName(), user.getLogin());

        user.setName(" ");
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void birthdayValidationTest() {
        assertTrue(violations.isEmpty());

        user.setBirthday(LocalDate.now().minusDays(1));
        violations = validator.validate(user);
        assertEquals(0, violations.size());

        user.setBirthday(LocalDate.now());
        violations = validator.validate(user);
        assertEquals(0, violations.size());

        user.setBirthday(LocalDate.now().plusDays(1));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }
}
