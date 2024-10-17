package ch.smartgridready.intermediary.repository;

import ch.smartgridready.intermediary.entity.ExternalInterfaceXml;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalInterfaceXmlRepository extends JpaRepository<ExternalInterfaceXml, Long> {

    List<ExternalInterfaceXml> findByName(String name);
}
