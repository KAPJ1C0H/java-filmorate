package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM rating_MPA";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating_MPA WHERE mpa_id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    public List<Mpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Mpa> findById(int mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }
}
