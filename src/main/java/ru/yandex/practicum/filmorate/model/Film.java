package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private MpaRating mpa;
    private final Set<Genre> genres = new HashSet<>();
    private final Set<Long> likes = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public List<Genre> getGenres() {
        return genres.stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());
    }

}
