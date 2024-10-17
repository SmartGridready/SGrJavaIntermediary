package ch.smartgridready.intermediary.repository;

import ch.smartgridready.intermediary.entity.ConfigurationValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationValueRepository extends JpaRepository<ConfigurationValue, Long> {
}
