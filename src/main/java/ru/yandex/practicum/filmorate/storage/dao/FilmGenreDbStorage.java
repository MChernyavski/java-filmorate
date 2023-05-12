package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("FilmGenreDbStorage")
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addGenreToFilm(long filmId, long genreId) {
        String sqlGenreFilm = "insert into film_genre (film_id, genre_id) values (?,?)";
       return (jdbcTemplate.update(sqlGenreFilm, filmId, genreId)) > 0;
    }

    @Override
    public boolean removeGenreFromFilm(long filmId, int genreId) {
        String sqlRemoveGenre = "delete from film_genre where film_id =? and genre_id = ?";
       return (jdbcTemplate.update(sqlRemoveGenre, filmId, genreId)) > 0;
    }

    @Override
    public List<FilmGenre> getGenreByFilm(long id) {
        String sqlGenreFilm = "select g.* from FILM_GENRE as fg join GENRES as g on fg.genre_id = g.genre_id " +
                "where fg.film_id = ? ORDER BY g.GENRE_ID";
         return jdbcTemplate.query(sqlGenreFilm, (rs, rowNum) -> mapToRowFilmGenre(rs), id);
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

