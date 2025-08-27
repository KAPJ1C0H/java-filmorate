package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LikeDbStorage extends BaseRepository<Film> {
    private static final String INSERT_QUERY = "INSERT INTO film_likes (film_id, user_liked_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_liked_id = ?";
    private static final String DELETE_ALL_FILM_LIKES_QUERY = "DELETE FROM film_likes WHERE film_id = ?";
    private static final String FIND_FILM_LIKES_QUERY = "SELECT user_liked_id FROM film_likes WHERE film_id = ?";
    private static final String FIND_ALL_FILMS_LIKES_QUERY = "SELECT * FROM film_likes";

    public LikeDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    public void addLike(Long filmId, Long userId) {
        update(INSERT_QUERY, filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        update(DELETE_QUERY, filmId, userId);
    }

    public void deleteAllFilmLikes(Long filmId) {
        update(DELETE_ALL_FILM_LIKES_QUERY, filmId);
    }

    public Collection<Long> getFilmLikes(Long filmId) {
        return jdbc.queryForList(FIND_FILM_LIKES_QUERY, Long.TYPE, filmId);
    }

    public Map<Long, Collection<Long>> findAllFilmsLikes() {
        Map<Long, Collection<Long>> likes = new HashMap<>();
        return jdbc.query(FIND_ALL_FILMS_LIKES_QUERY, (ResultSet resultSet) -> {
            while (resultSet.next()) {
                Long filmId = resultSet.getLong("film_id");
                Long likeId = resultSet.getLong("user_liked_id");
                likes.computeIfAbsent(filmId, k -> new ArrayList<>()).add(likeId);
            }
            return likes;
        });
    }
}
