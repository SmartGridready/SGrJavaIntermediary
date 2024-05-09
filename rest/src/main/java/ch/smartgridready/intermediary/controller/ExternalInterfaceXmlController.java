package ch.smartgridready.intermediary.controller;

import ch.smartgridready.intermediary.entity.ExternalInterfaceXml;
import ch.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;
import ch.smartgridready.intermediary.repository.ExternalInterfaceXmlRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
class ExternalInterfaceXmlController {

	private final ExternalInterfaceXmlRepository repository;

	ExternalInterfaceXmlController(ExternalInterfaceXmlRepository repository) {
		this.repository = repository;
	}

	@PostMapping("/eiXml/{fileName}")
	EntityModel<ExternalInterfaceXml> save(
			@PathVariable("fileName") String fileName,
			@RequestParam("file") MultipartFile file
		) throws IOException {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			final var xml = reader.lines().collect(Collectors.joining(System.lineSeparator()));
			var eiXmlList = repository.findByName(fileName);
			var eiXml = eiXmlList.stream().findFirst().orElseGet(() -> new ExternalInterfaceXml(fileName, xml));
			return EntityModel.of(repository.save(eiXml));
		}
	}
	@GetMapping("/eiXml/{name}")
	ExternalInterfaceXml externalInterfaceXml(@PathVariable("name") String fileName) {
		return repository.findByName(fileName)
				.stream().findFirst()
				.orElseThrow(() -> new ExtIfXmlNotFoundException(fileName));
	}

	@DeleteMapping("/eiXml/{name}")
	void delete(@PathVariable("name") String name) {
		var eiXml = repository.findByName(name).stream().findFirst().orElseThrow(() -> new ExtIfXmlNotFoundException(name));
		repository.delete(eiXml);
	}

	@GetMapping("/eiXml")
	List<ExternalInterfaceXml> getAll() {
		return repository.findAll();
	}
}
