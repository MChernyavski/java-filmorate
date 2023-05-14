package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Primary
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("releaseDate", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpaId", film.getMpa().getId());

        Number userKey = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(values));
        film.setId(userKey.longValue());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdFilm = "UPDATE FILMS SET NAME = :name, DESCRIPTION = :description, RELEASE_DATE = :releaseDate, " +
                "DURATION = :duration, MPA_ID = :mpaId WHERE FILM_ID = :id";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId())
                .addValue("id", film.getId());
        int rowsAffected = namedParameterJdbcTemplate.update(sqlUpdFilm, sqlParameterSource);
        if (rowsAffected == 0) {
            throw new NotFoundException("Не существует фильма с id {}: " + film.getId());
        }
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        try {
            String sqlFilmById = "SELECT *, MR.NAME FROM FILMS JOIN MPA_RATING MR on MR.MPA_ID = FILMS.MPA_ID " +
                    "WHERE FILM_ID = ? ";
            return jdbcTemplate.queryForObject(sqlFilmById, (rs, rowNum) -> mapRowToFilm(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Фильм с id = %s не найден", id));
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlAllFilms = "SELECT f.*, m.name as mpa_name " +
                "FROM FILMS AS f JOIN MPA_RATING AS m ON f.MPA_ID = m.MPA_ID";
        return namedParameterJdbcTemplate.query(sqlAllFilms, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        String sqlPopularFilms = "SELECT f.*, mr.NAME FROM FILMS AS f JOIN MPA_RATING AS mr ON f.MPA_ID = mr.MPA_ID " +
                "LEFT JOIN (SELECT FILM_ID, COUNT(USER_ID) AS all_likes FROM LIKES GROUP BY FILM_ID ORDER BY all_likes) " +
                "as toplist ON f.FILM_ID = toplist.FILM_ID ORDER BY toplist.all_likes DESC LIMIT ?";
        return jdbcTemplate.query(sqlPopularFilms, (rs, rowNum) -> mapRowToFilm(rs), count);
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("MPA_RATING.NAME");

        MpaRating mpa = MpaRating.builder()
                .id(mpaId)
                .name(mpaName)
                .build();

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .build();
    }
}

