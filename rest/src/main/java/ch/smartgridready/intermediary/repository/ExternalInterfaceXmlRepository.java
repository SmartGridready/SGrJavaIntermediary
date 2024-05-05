package ch.smartgridready.intermediary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface ExternalInterfaceXmlRepository extends JpaRepository<ExternalInterfaceXml, Long> {

    List<ExternalInterfaceXml> findByName(String name);
}
