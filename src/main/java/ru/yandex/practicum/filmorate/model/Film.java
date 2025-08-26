package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@ToString
@EqualsAndHashCode
@Getter
public class Film {
    @Setter
    private long id;
    @Setter
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Setter
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @Setter
    @FilmReleaseDateConstraint
    private LocalDate releaseDate;
    @Setter
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private long duration;
    private Set<Long> usersLikes = new HashSet<>();

    public Film(long id, String name, String description, LocalDate releaseDate, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
