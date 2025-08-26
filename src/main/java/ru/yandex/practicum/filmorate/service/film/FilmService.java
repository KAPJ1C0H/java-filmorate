package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.LikeDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;

    public Film addLike(Long filmId, Long userId) {
        likeDbStorage.addLike(filmId, userId);
        return filmStorage.findById(filmId);
    }

    public Film deleteLike(Long filmId, Long userId) {
        likeDbStorage.deleteLike(filmId, userId);
        return filmStorage.findById(filmId);
    }

    public Collection<Film> getPopularFilm(Long count) {
        return filmStorage.getPopularFilms(count);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film delete(Long id) {
        return filmStorage.delete(id);
    }
}
