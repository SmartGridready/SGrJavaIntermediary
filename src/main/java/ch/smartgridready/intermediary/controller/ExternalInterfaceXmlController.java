package ch.smartgridready.intermediary.controller;

import ch.smartgridready.intermediary.entity.ExternalInterfaceXml;
import ch.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;
import ch.smartgridready.intermediary.repository.ExternalInterfaceXmlRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@Tag(name = "External-Interface-XML Controller", description = "Allows the management of the external-interface-xml files (EI-XML)")
class ExternalInterfaceXmlController {

	private final ExternalInterfaceXmlRepository repository;

	ExternalInterfaceXmlController(ExternalInterfaceXmlRepository repository) {
		this.repository = repository;
	}

	@Operation(description = "Used to add new EI-XML file")
	@PostMapping("/eiXml/{fileName}")
	@ApiResponse(description = "The EI-XML file name and content")
	ExternalInterfaceXml save(
			@PathVariable("fileName") @Parameter(description = "The filename of the EI-XML") String fileName,
			@RequestParam("file") @Parameter(description = "File as multipart file upload content.") MultipartFile file
		) throws IOException {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			final var xml = reader.lines().collect(Collectors.joining(System.lineSeparator()));
			var eiXmlList = repository.findByName(fileName);
			var eiXml = eiXmlList.stream().findFirst().orElseGet(() -> new ExternalInterfaceXml(fileName, xml));
			eiXml.setXml(xml); // overwrite XML
			return repository.save(eiXml);
		}
	}

	@Operation(description = "Finds an EI-XML by name.")
	@ApiResponse(description = "The EI-XML file name and content")
	@GetMapping("/eiXml/{name}")
	ExternalInterfaceXml externalInterfaceXml(@PathVariable("name") @Parameter(description = "The name of the EI-XML file") String fileName) {
		return repository.findByName(fileName)
				.stream().findFirst()
				.orElseThrow(() -> new ExtIfXmlNotFoundException(fileName));
	}

	@Operation(description = "Deletes an EI-XML")
	@DeleteMapping("/eiXml/{name}")
	void delete(@PathVariable("name") @Parameter(description = "The EI-XML name") String name) {
		var eiXml = repository.findByName(name).stream().findFirst().orElseThrow(() -> new ExtIfXmlNotFoundException(name));
		repository.delete(eiXml);
	}

	@Operation(description = "Get a list of all EI-XML")
	@ApiResponse(description = "A list of all EI-XMLs with their names and content")
	@GetMapping("/eiXml")
	List<ExternalInterfaceXml> getAll() {
		return repository.findAll();
	}
}
