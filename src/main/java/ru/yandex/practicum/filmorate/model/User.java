package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class User {
    @Getter
    private Long id;

    @Getter
    @NotBlank
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    private String email;

    @Getter
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @Getter
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public String getName() {
        return name == null || name.isBlank() ? login : name;
    }
}
