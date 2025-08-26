package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import({FilmDbStorage.class,
        FilmRowMapper.class,
        MpaDbStorage.class,
        MpaRowMapper.class,
        LikeDbStorage.class,
        GenreDbStorage.class,
        GenreRowMapper.class,
        UserDbStorage.class,
        UserRowMapper.class,
        FriendDbStorage.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final UserDbStorage userDbStorage;
    Film film1;
    Film film2;
    User user1;

    @BeforeEach
    void beforeALL() {
        film1 = Film.builder()
                .name("IT")
                .description("Children play with a red balloon")
                .releaseDate(LocalDate.of(2017, 9, 7))
                .duration(135)
                .mpa(new Mpa(4, null))
                .genres(Set.of(new Genre(4, null)))
                .build();
        film2 = Film.builder()
                .name("Film2")
                .description("Film about Java")
                .releaseDate(LocalDate.of(2000, 9, 7))
                .duration(120)
                .mpa(new Mpa(1, null))
                .genres(Set.of(new Genre(4, null), new Genre(5, null)))
                .build();
        user1 = User.builder()
                .email("ivan@yandex.ru")
                .login("Ivan123")
                .name("Ivan")
                .birthday(LocalDate.of(1996, 12, 3))
                .build();
    }

    @Test
    void createAndFindById() {
        filmDbStorage.create(film1);
        assertThat(filmDbStorage.findById(1L)).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void findAll() {
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        assertThat(filmDbStorage.findAll()).hasSize(2);
    }

    @Test
    void update() {
        filmDbStorage.create(film1);
        film1.setName("Update");
        filmDbStorage.update(film1);
        assertThat(filmDbStorage.findById(1L)).hasFieldOrPropertyWithValue("name", "Update");
    }

    @Test
    void delete() {
        filmDbStorage.create(film1);
        filmDbStorage.delete(1L);
        assertThat(filmDbStorage.findAll()).isEmpty();
    }

    @Test
    void getPopularFilms() {
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        userDbStorage.create(user1);
        likeDbStorage.addLike(1L, 1L);
        Film film = filmDbStorage.getPopularFilms(1L).iterator().next();
        assertThat(film).hasFieldOrPropertyWithValue("name", "IT");
    }
}
