package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FilmTest {

    @Autowired
    private Validator validator;
    private Film film;
    private Set<ConstraintViolation<Film>> violations;
    private String stringLength199 = new String(new char[199]).replace("\0", "A");

    @BeforeEach
    void setUp() {
        film = new Film(1L, "IT", "Children play with a red balloon",
                LocalDate.of(2017, 9, 7), 135);
        violations = validator.validate(film);
    }

    @Test
    void nameValidationTest() {
        assertTrue(violations.isEmpty());

        film.setName(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());

        film.setName(" ");
        violations = validator.validate(film);
        assertEquals(1, violations.size());

        film.setName("I T");
        violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void descriptionValidationTest() {
        assertTrue(violations.isEmpty());

        film.setDescription(null);
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film.setDescription(" ");
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film.setDescription(stringLength199);
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film.setDescription(stringLength199 + "A");
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film.setDescription(stringLength199 + "AA");
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    void durationValidationTest() {
        assertTrue(violations.isEmpty());

        film.setDuration(0);
        violations = validator.validate(film);
        assertEquals(1, violations.size());

        film.setDuration(-1);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    void releaseDateValidationTest() {
        assertTrue(violations.isEmpty());

        film.setReleaseDate(FilmController.MOVIE_BIRTHDAY.plusDays(1));
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film.setReleaseDate(FilmController.MOVIE_BIRTHDAY);
        violations = validator.validate(film);
        assertEquals(0, violations.size());

        film.setReleaseDate(FilmController.MOVIE_BIRTHDAY.minusDays(1));
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }
}
