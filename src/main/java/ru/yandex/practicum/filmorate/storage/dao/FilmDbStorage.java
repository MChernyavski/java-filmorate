package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository("FilmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdateFilm = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ?" +
                "WHERE FILM_ID = ?";
        if (jdbcTemplate.update(sqlUpdateFilm, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpaRating().getId(), film.getId()) > 0) {
            return film;
    }
        log.error("ERROR: Не существует фильма с таким id {} ", film.getId());
        throw new NotFoundException("Отсутствует фильм c id " + film.getId());
}

    @Override
    public List<Film> getAllFilms() {
        String sqlAllFilms = "select f.*, mpa.name from films as f join mpa_rating as mpa on mpa.mpa_id=f.mpa_id";
        return jdbcTemplate.query(sqlAllFilms, (rs, rowNum) -> makeRowToFilm(rs));
    }

    @Override
    public Film getFilmById(long id) {
        String sqlFilmById = "select f.*, mpa.name from films as f join mpa_rating as mpa on mpa.mpa_id=f.mpa_id " +
                "where f.film_id = ?";
        try { return jdbcTemplate.queryForObject(sqlFilmById, (rs, rowNum) -> makeRowToFilm(rs), id);
    } catch (NotFoundException e) {
            log.error("ERROR: Не существует фильма с таким id {} ", id);
            throw new NotFoundException("Отсутствует фильма c id " + id);
        }
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        String sqlPopularFilms = "select f.* from films as f " +
                "left join likes as l on l.film_id=f.film_id " +
                "group by f.film_id order by count(l.user_id) DESC limit ?";
        return jdbcTemplate.query(sqlPopularFilms, (rs, rowNum) -> makeRowToFilm(rs), count);
    } //вроде тут всё. добавить нечего

    private Film makeRowToFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("name");
        MpaRating mpaRating = MpaRating.builder()
                .id(mpaId)
                .name(mpaName)
                .build();
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();
    }
}
