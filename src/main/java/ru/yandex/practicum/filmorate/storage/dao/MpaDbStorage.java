package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAllMpa() {
        String sqlAllMpa = "select * from mpa_rating";
        return jdbcTemplate.query(sqlAllMpa, (rs, rowNum) -> mapRowToMpa(rs));
    }

    @Override
    public MpaRating getMpaById(int id) {
        try {
            String sqlMpaById = "select * from mpa_rating where mpa_id = ?";
            return jdbcTemplate.queryForObject(sqlMpaById, (rs, rowNum) -> mapRowToMpa(rs), id);
        } catch (DataRetrievalFailureException e) {
            throw new NotFoundException("Не существует Mpa с таким id" + id);
        }
    }

    private MpaRating mapRowToMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        return MpaRating.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}
