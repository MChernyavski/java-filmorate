package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class  Film {
    private long id;
    @NotBlank
    private String name; // название не может быть пустым
    @NotBlank
    @Size(max = 200) //описание должно быть не больше 200 символов
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration; //продолжительность фильма должна быть положительной
    @NotNull
    private MpaRating mpaRating;
    private final Set<Genre> genres = new HashSet<>();
    private final Set<Long> likes = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpaRating.getId());
        return values;
    }
}
