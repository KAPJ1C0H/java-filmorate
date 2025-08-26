package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private MpaDbStorage mpaDbStorage;
    private GenreDbStorage genreDbStorage;
    private LikeDbStorage likeDbStorage;
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films f, rating_mpa m " +
            "WHERE f.rating_id = m.mpa_id AND f.id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films f, rating_mpa m " +
            "WHERE f.rating_id = m.mpa_id";
    private static final String FIND_POPULAR_FILMS_QUERY = "SELECT * FROM films f LEFT JOIN rating_mpa m " +
            "ON f.id = m.mpa_id LEFT JOIN (SELECT film_id, COUNT(film_id) AS likes FROM film_likes GROUP BY film_id) fl " +
            "ON f.id = fl.film_id ORDER BY likes DESC LIMIT ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, " +
            "release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, MpaDbStorage mpaDbStorage,
                         GenreDbStorage genreDbStorage, LikeDbStorage likeDbStorage) {
        super(jdbc, mapper, Film.class);
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = findMany(FIND_ALL_QUERY);
        Map<Long, Set<Genre>> genres = genreDbStorage.findAllFilmsGenres();
        Map<Long, Collection<Long>> likes = likeDbStorage.findAllFilmsLikes();
        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), new LinkedHashSet<>()));
            film.setUsersLikes(likes.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }

    @Override
    public Film findById(Long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
        film.setGenres(new HashSet<>(genreDbStorage.findByFilmId(id)));
        film.setUsersLikes(likeDbStorage.getFilmLikes(id));
        return film;
    }

    @Override
    public Film create(Film film) {
        Mpa mpa = mpaDbStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new ValidationException("Указанный Mpa не существует."));
        Collection<Genre> genres = genreDbStorage.findManyByList(film.getGenres());
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        film.setMpa(mpa);
        film.setGenres(new LinkedHashSet<>(genres));
        genreDbStorage.setFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        mpaDbStorage.findById(film.getMpa().getId());
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return findById(film.getId());
    }

    @Override
    public Film delete(Long id) {
        Film film = findById(id);
        genreDbStorage.deleteGenres(id);
        if (!film.getUsersLikes().isEmpty()) likeDbStorage.deleteAllFilmLikes(id);
        update(DELETE_QUERY, id);
        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        Collection<Film> films = findMany(FIND_POPULAR_FILMS_QUERY, count);
        Map<Long, Set<Genre>> genres = genreDbStorage.findAllFilmsGenres();
        Map<Long, Collection<Long>> likes = likeDbStorage.findAllFilmsLikes();
        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), new LinkedHashSet<>()));
            film.setUsersLikes(likes.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }
}
