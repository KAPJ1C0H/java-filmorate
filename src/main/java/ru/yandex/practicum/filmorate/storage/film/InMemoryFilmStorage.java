package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private long filmId = 0;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(Long id) {
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    @Override
    public Film create(Film film) {
        film.setId(getFilmId());
        films.put(film.getId(), film);
        log.info("Новый фильм: {} Общее количество фильмов в списке: {}", film, films.size());
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Старые данные о фильме: {}", films.get(film.getId()));
        Film newFilm = films.computeIfPresent(film.getId(), (key, value) -> value = film);
        if (newFilm != null) {
            log.info("Новые данные о фильме: {}", films.get(film.getId()));
            return newFilm;
        }
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    @Override
    public Film delete(Long id) {
        return Optional.ofNullable(films.remove(id))
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        if (count == null || count <= 0) {
            count = 10L;
        }

        return films.values().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private long getFilmId() {
        return ++filmId;
    }

    private void resetFilmId() {
        filmId = 0;
    }
}