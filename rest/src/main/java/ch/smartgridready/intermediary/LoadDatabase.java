package ch.smartgridready.intermediary;

import ch.smartgridready.intermediary.repository.ExternalInterfaceXmlRepository;
import ch.smartgridready.intermediary.service.IntermediaryService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	private final IntermediaryService intermediaryService;

	@Bean
	CommandLineRunner initDatabase(ExternalInterfaceXmlRepository repository) {
		log.info("Going to load devices.");
		return args -> intermediaryService.loadDevices();
	}
}
