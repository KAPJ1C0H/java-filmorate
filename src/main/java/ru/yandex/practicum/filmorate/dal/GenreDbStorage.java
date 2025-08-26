package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_MANY_QUERY = "SELECT * FROM genres WHERE id IN (%s)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String DELETE_ALL_GENRES_FILM_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT id, name FROM genres g, film_genres fg " +
            "WHERE g.id = fg.genre_id AND fg.film_id = ?";
    private static final String FIND_ALL_FILMS_GENRES_QUERY = "SELECT film_id, genre_id, name FROM film_genres fg, " +
            "genres g WHERE fg.genre_id = g.id";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Collection<Genre> findManyByList(Collection<Genre> genres) {
        if (genres.isEmpty()) return new ArrayList<>();
        List<Integer> genresId = new ArrayList<>(genres.stream().map(Genre::getId).toList());
        String inSql = String.join(",", Collections.nCopies(genresId.size(), "?"));

        List<Genre> result = jdbc.query(
                String.format(FIND_MANY_QUERY, inSql),
                genresId.toArray(),
                mapper
        );
        if (result.size() != genresId.size()) throw new ValidationException("Указан Genre с неверным id");
        return result;
    }

    public Optional<Genre> findById(int genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public Collection<Genre> findByFilmId(long filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }

    public Map<Long, Set<Genre>> findAllFilmsGenres() {
        Map<Long, Set<Genre>> genres = new HashMap<>();
        return jdbc.query(FIND_ALL_FILMS_GENRES_QUERY, (ResultSet resultSet) -> {
            while (resultSet.next()) {
                Long filmId = resultSet.getLong("film_id");
                Integer genreId = resultSet.getInt("genre_id");
                String genreName = resultSet.getString("name");
                genres.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(new Genre(genreId, genreName));
            }
            return genres;
        });
    }

    public void setFilmGenres(Long filmId, Collection<Genre> genres) {
        if (genres.isEmpty()) return;
        List<Integer> genresId = new ArrayList<>(genres.stream().map(Genre::getId).toList());
        batchUpdateBase(INSERT_QUERY, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, genresId.get(i));
            }

            @Override
            public int getBatchSize() {
                return genresId.size();
            }
        });
    }

    public void deleteGenres(Long filmId) {
        update(DELETE_ALL_GENRES_FILM_QUERY, filmId);
    }
}
