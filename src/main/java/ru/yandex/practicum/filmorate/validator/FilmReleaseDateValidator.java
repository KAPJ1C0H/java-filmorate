package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDateConstraint, LocalDate> {

    @Override
    public void initialize(FilmReleaseDateConstraint dateConstraint) {
    }

    @Override
    public boolean isValid(LocalDate filmReleaseDate, ConstraintValidatorContext cxt) {
        return FilmController.MOVIE_BIRTHDAY.isBefore(filmReleaseDate)
                || FilmController.MOVIE_BIRTHDAY.isEqual(filmReleaseDate);
    }
}
