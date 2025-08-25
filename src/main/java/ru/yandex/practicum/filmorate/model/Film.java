package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private long id;
    private String name;
    private String description;
    private Instant releaseDate;
    private Duration duration;
}