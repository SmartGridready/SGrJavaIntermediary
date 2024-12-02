package com.smartgridready.intermediary.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartgridready.intermediary.entity.ExternalInterfaceXml;
import com.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;
import com.smartgridready.intermediary.repository.ExternalInterfaceXmlRepository;
import com.smartgridready.intermediary.service.IntermediaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "External-Interface-XML Controller", description = "Allows the management of the external-interface-xml files (EI-XML)")
class ExternalInterfaceXmlController {

	private static final Logger LOG = LoggerFactory.getLogger(ExternalInterfaceXmlController.class);

	private final ExternalInterfaceXmlRepository eiXmlRepository;

	private final IntermediaryService intermediaryService;

	ExternalInterfaceXmlController(ExternalInterfaceXmlRepository eiXmlRepository,
			IntermediaryService intermediaryService) {
		this.eiXmlRepository = eiXmlRepository;
		this.intermediaryService = intermediaryService;
	}

	@Operation(description = "Used to add new EI-XML file")
	@PostMapping("/eiXml/{fileName}")
	@ApiResponse(description = "The EI-XML file name")
	ExternalInterfaceXml save(
			@PathVariable("fileName") @Parameter(description = "The filename of the EI-XML") String fileName)
			throws IOException, InterruptedException {

		// load EI-XML file from GitHub
		final var eiXmlFile = loadExternalInterfaceFromGitHub(fileName);

		// update or create EI-XML
		final var eiXmlList = eiXmlRepository.findByName(fileName);
		final var eiXml = eiXmlList.stream().findFirst().orElseGet(() -> new ExternalInterfaceXml(fileName, eiXmlFile));
		eiXml.setXml(eiXmlFile); // overwrite XML
		final var newEiXml = eiXmlRepository.save(eiXml);

		// reload all devices that are based on this EI-XML file
		intermediaryService.loadDevicesBasedOnEiXMl(fileName);

		return newEiXml;
	}
	
	private String loadExternalInterfaceFromGitHub(String fileName) throws IOException, InterruptedException {
		final var baseUri = "https://raw.githubusercontent.com/SmartGridready/SGrSpecifications/refs/heads/master/XMLInstances/ExtInterfaces/";
		final var uri = baseUri + fileName;
		final var client = HttpClient.newHttpClient();
		final var request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
		LOG.info("Loading EI-XML file for file name '{}' from URI '{}'", fileName, uri);
		final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}

	@Operation(description = "Finds an EI-XML by name.")
	@ApiResponse(description = "The EI-XML file name and content")
	@GetMapping("/eiXml/{name}")
	ExternalInterfaceXml externalInterfaceXml(@PathVariable("name") @Parameter(description = "The name of the EI-XML file") String fileName) {
		return eiXmlRepository.findByName(fileName)
				.stream().findFirst()
				.orElseThrow(() -> new ExtIfXmlNotFoundException(fileName));
	}

	@Operation(description = "Deletes an EI-XML")
	@DeleteMapping("/eiXml/{name}")
	void delete(@PathVariable("name") @Parameter(description = "The EI-XML name") String name) {
		var eiXml = eiXmlRepository.findByName(name).stream().findFirst().orElseThrow(() -> new ExtIfXmlNotFoundException(name));
		eiXmlRepository.delete(eiXml);
	}

	@Operation(description = "Get a list of all EI-XML")
	@ApiResponse(description = "A list of all EI-XMLs with their names and content")
	@GetMapping("/eiXml")
	List<ExternalInterfaceXml> getAll() {
		return eiXmlRepository.findAll();
	}
}
