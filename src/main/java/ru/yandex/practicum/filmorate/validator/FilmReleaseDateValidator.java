package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDateConstraint, LocalDate> {

    @Override
    public void initialize(FilmReleaseDateConstraint dateConstraint) {
    }

    @Override
    public boolean isValid(LocalDate filmReleaseDate, ConstraintValidatorContext cxt) {
        return FilmStorage.MOVIE_BIRTHDAY.isBefore(filmReleaseDate)
                || FilmStorage.MOVIE_BIRTHDAY.isEqual(filmReleaseDate);
    }
}
