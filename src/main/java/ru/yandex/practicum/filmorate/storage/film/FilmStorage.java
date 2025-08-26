package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

public interface FilmStorage {

    LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    Collection<Film> findAll();

    Film findById(Long id);

    Film create(Film film);

    Film update(Film film);

    Film delete(Long id);
}
