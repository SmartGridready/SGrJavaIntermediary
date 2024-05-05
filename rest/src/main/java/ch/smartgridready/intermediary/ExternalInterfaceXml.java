package ch.smartgridready.intermediary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
class ExternalInterfaceXml {

	private @Id @GeneratedValue Long id;

	@Column(nullable = false, length = 256, unique = true)
	private String name;

	@Column(nullable = false, length = 1000000)
	private String xml;

	ExternalInterfaceXml() {}

	ExternalInterfaceXml(String name, String xml) {

		this.name = name;
		this.xml = xml;
	}
}
