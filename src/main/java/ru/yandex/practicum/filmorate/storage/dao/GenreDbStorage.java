package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository ("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlAllGenres = "select * from genres";
        return jdbcTemplate.query(sqlAllGenres, (rs, rowNum) -> mapToRowGenre(rs));
    }

    @Override
    public Genre getGenreById(int id) {
        try {   String sqlGenreId = "select * from genres where genre_id = ?";
            return jdbcTemplate.queryForObject(sqlGenreId, (rs, rowNum) -> mapToRowGenre(rs), id);
        } catch (DataRetrievalFailureException e) {
            throw new NotFoundException("Не существует жанра с таким id" + id);
        }
    }

    @Override
    public List<Genre> getGenreByFilm(long filmId) {
        String sqlGenreFilm = "select g.* from FILM_GENRE as fg join GENRES as g on fg.genre_id = g.genre_id " +
                "where fg.film_id = ? ORDER BY g.GENRE_ID";
        return jdbcTemplate.query(sqlGenreFilm, (rs, rowNum) -> mapToRowGenre(rs), filmId);
    }

    private Genre mapToRowGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }
}
