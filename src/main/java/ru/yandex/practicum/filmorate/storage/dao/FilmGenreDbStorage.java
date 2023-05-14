package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository("FilmGenreDbStorage")
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addGenreToFilm(long filmId, long genreId) {
        String sqlGenreFilm = "insert into film_genre (film_id, genre_id) values (?,?)";
        jdbcTemplate.update(sqlGenreFilm, filmId, genreId);
    }

    @Override
    public void removeGenreFromFilm(long filmId) {
        String sqlRemoveGenre = "delete from film_genre where film_id =?";
        jdbcTemplate.update(sqlRemoveGenre, filmId);
    }

    private FilmGenre mapToRowFilmGenre(ResultSet rs) throws SQLException {
        int genreId = rs.getInt("genre_id");
        long filmId = rs.getLong("film_id");
        return FilmGenre.builder()
                .genreId(genreId)
                .filmId(filmId)
                .build();
    }
}

