package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class User {
    private long id;
    @NotBlank
    @Email
    private String email; // электронная почта не может быть пустой и должна содержать символ @;
    @NotBlank
    @Pattern(regexp = "\\S+") //логин не может быть пустым и содержать пробелы;
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday; //дата рождения не может быть в будущем.
    private final Set<Long> friends = new HashSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("name", name);
        values.put("login", login);
        values.put("birthday", birthday);
        return values;
    }
}
