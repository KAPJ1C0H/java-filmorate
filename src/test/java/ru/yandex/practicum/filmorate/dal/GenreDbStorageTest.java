package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class,
        GenreRowMapper.class})
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void findById() {
        Genre genre = genreDbStorage.findById(1).orElseThrow();
        assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void findAll() {
        assertThat(genreDbStorage.findAll()).isNotEmpty();
    }
}
