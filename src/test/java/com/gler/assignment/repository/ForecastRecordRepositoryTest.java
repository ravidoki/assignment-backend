package com.gler.assignment.repository;

import com.gler.assignment.model.ForecastRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for ForecastRecordRepository.
 * Uses @DataJpaTest for testing JPA repositories with embedded H2 database.
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
})
class ForecastRecordRepositoryTest {

    @Autowired
    private ForecastRecordRepository repository;

    @Test
    void save_shouldPersistForecastRecord() {
        // Given
        LocalDate today = LocalDate.of(2025, 1, 15);
        ForecastRecord item = new ForecastRecord(today, 25.5, 80.0, 15.2);

        // When
        ForecastRecord saved = repository.save(item);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDate()).isEqualTo(today);
        assertThat(saved.getMaxTemperature()).isEqualTo(25.5);
        assertThat(saved.getMaxHumidity()).isEqualTo(80.0);
        assertThat(saved.getMaxWindSpeed()).isEqualTo(15.2);
    }

    @Test
    void findById_shouldReturnForecastRecord_whenExists() {
        // Given
        LocalDate today = LocalDate.of(2025, 1, 15);
        ForecastRecord item = new ForecastRecord(today, 20.0, 70.0, 10.0);
        ForecastRecord saved = repository.save(item);

        // When
        Optional<ForecastRecord> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDate()).isEqualTo(today);
        assertThat(found.get().getMaxTemperature()).isEqualTo(20.0);
        assertThat(found.get().getMaxHumidity()).isEqualTo(70.0);
        assertThat(found.get().getMaxWindSpeed()).isEqualTo(10.0);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        // When
        Optional<ForecastRecord> found = repository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllForecastRecords() {
        // Given
        LocalDate today = LocalDate.of(2025, 1, 15);
        LocalDate yesterday = LocalDate.of(2025, 1, 14);

        ForecastRecord item1 = new ForecastRecord(today, 25.0, 75.0, 12.0);
        ForecastRecord item2 = new ForecastRecord(yesterday, 22.0, 72.0, 8.0);

        repository.save(item1);
        repository.save(item2);

        // When
        List<ForecastRecord> allRecords = repository.findAll();

        // Then
        assertThat(allRecords).hasSize(2);
        assertThat(allRecords)
            .extracting(ForecastRecord::getDate)
            .containsExactlyInAnyOrder(today, yesterday);
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        // Given
        LocalDate today = LocalDate.of(2025, 1, 15);
        ForecastRecord item = new ForecastRecord(today, 18.0, 65.0, 5.0);
        ForecastRecord saved = repository.save(item);

        // When & Then
        assertThat(repository.existsById(saved.getId())).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        // When & Then
        assertThat(repository.existsById(999L)).isFalse();
    }

    @Test
    void deleteById_shouldRemoveForecastRecord() {
        // Given
        LocalDate today = LocalDate.of(2025, 1, 15);
        ForecastRecord item = new ForecastRecord(today, 30.0, 85.0, 20.0);
        ForecastRecord saved = repository.save(item);

        assertThat(repository.existsById(saved.getId())).isTrue();

        // When
        repository.deleteById(saved.getId());

        // Then
        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void delete_shouldRemoveForecastRecord() {
        // Given
        LocalDate today = LocalDate.of(2025, 1, 15);
        ForecastRecord item = new ForecastRecord(today, 28.0, 78.0, 18.0);
        ForecastRecord saved = repository.save(item);

        assertThat(repository.existsById(saved.getId())).isTrue();

        // When
        repository.delete(saved);

        // Then
        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void save_shouldHandleNullValues() {
        // Given - item with null values for optional fields
        LocalDate today = LocalDate.of(2025, 1, 15);
        ForecastRecord item = new ForecastRecord(today, null, null, null);

        // When
        ForecastRecord saved = repository.save(item);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDate()).isEqualTo(today);
        assertThat(saved.getMaxTemperature()).isNull();
        assertThat(saved.getMaxHumidity()).isNull();
        assertThat(saved.getMaxWindSpeed()).isNull();
    }

    @Test
    void count_shouldReturnNumberOfRecords() {
        // Given
        LocalDate today = LocalDate.of(2025, 1, 15);
        repository.save(new ForecastRecord(today, 25.0, 75.0, 12.0));
        repository.save(new ForecastRecord(today.minusDays(1), 22.0, 72.0, 8.0));

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }
}
