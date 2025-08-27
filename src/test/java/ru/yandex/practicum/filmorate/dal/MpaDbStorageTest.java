package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class,
        MpaRowMapper.class})
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void findById() {
        Mpa mpa = mpaDbStorage.findById(1).orElseThrow();
        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void findAll() {
        assertThat(mpaDbStorage.findAll()).isNotEmpty();
    }
}
