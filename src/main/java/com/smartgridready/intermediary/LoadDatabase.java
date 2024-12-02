package com.smartgridready.intermediary;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartgridready.intermediary.repository.ExternalInterfaceXmlRepository;
import com.smartgridready.intermediary.service.IntermediaryService;

@AllArgsConstructor
@Configuration
class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	private final IntermediaryService intermediaryService;

	@SuppressWarnings("unused")
	@Bean
	CommandLineRunner initDatabase(ExternalInterfaceXmlRepository repository) {
		log.info("Going to load devices.");
		return args -> intermediaryService.loadDevices();
	}
}
