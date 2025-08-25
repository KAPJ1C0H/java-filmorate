package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    public static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private Map<Long, Film> films = new HashMap<>();
    private int filmId = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getFilmId());
        films.put(film.getId(), film);
        log.info("Новый фильм: {} Общее количество фильмов в списке: {}", film, films.size());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.info("Старые данные о фильме: {}", oldFilm);
            oldFilm = newFilm;
            log.info("Новые данные о фильме: {}", oldFilm);
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private int getFilmId() {
        return ++filmId;
    }

    private void resetFilmId() {
        filmId = 0;
    }
}
