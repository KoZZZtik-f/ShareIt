package ru.yandex.practicum.shareit.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class DatabaseCleanupService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCleanupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void cleanupDatabase() {
        try {
            // Чтение SQL-скрипта из resources
            ClassPathResource resource = new ClassPathResource("cleanup.sql");
            String sqlScript = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            // Разделение скрипта на отдельные запросы
            String[] queries = sqlScript.split(";");

            // Выполнение каждого запроса по отдельности (без этого не срабатывало)
            Arrays.stream(queries)
                    .map(String::trim) // Удаление лишних пробелов
                    .filter(query -> !query.isEmpty()) // Игнорирование пустых строк
                    .forEach(jdbcTemplate::execute);

            System.out.println("Database cleanup completed successfully.");
        } catch (Exception e) {
            System.err.println("Failed to cleanup database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}