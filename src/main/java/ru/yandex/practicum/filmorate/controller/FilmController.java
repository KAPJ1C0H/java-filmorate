
package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll(){
        return films.values();
    }

    @PostMapping
    public Film create (@RequestBody Film film) {

    }

    @PutMapping
    public Film update (@RequestBody Film newFilm) {

    }
}