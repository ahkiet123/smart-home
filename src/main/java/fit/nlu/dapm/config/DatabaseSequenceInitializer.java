package fit.nlu.dapm.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSequenceInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSequenceInitializer.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseSequenceInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncSequences() {
        try {
            List<Map<String, Object>> serialTables = jdbcTemplate.queryForList("""
                    SELECT schemaname,
                           tablename,
                           pg_get_serial_sequence(format('%I.%I', schemaname, tablename), 'id') AS sequence_name
                    FROM pg_tables
                    WHERE schemaname = current_schema()
                    """);

            int syncedCount = 0;
            for (Map<String, Object> tableInfo : serialTables) {
                String schemaName = (String) tableInfo.get("schemaname");
                String tableName = (String) tableInfo.get("tablename");
                String sequenceName = (String) tableInfo.get("sequence_name");

                if (sequenceName == null || sequenceName.isBlank()) {
                    continue;
                }

                String syncSql = String.format(
                        "SELECT setval('%s'::regclass, COALESCE((SELECT MAX(id) FROM \"%s\".\"%s\"), 0) + 1, false)",
                        escapeSqlLiteral(sequenceName),
                        escapeSqlIdentifier(schemaName),
                        escapeSqlIdentifier(tableName));

                jdbcTemplate.execute(syncSql);
                syncedCount++;
                logger.info("Synchronized sequence for table {}.{}", schemaName, tableName);
            }

            if (syncedCount == 0) {
                logger.info("No serial/identity id sequences found to synchronize in current schema");
            }
        } catch (DataAccessException | IllegalArgumentException ex) {
            // Ignore on non-PostgreSQL environments.
            logger.debug("Skip automatic sequence synchronization: {}", ex.getMessage());
        }
    }

    private String escapeSqlLiteral(String value) {
        return value == null ? "" : value.replace("'", "''");
    }

    private String escapeSqlIdentifier(String value) {
        return value == null ? "" : value.replace("\"", "\"\"");
    }
}