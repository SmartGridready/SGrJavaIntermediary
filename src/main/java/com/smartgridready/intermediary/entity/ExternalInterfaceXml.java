package com.smartgridready.intermediary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;


/**
 * Entity for external interface.
 */
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class ExternalInterfaceXml
{
    private @Id @GeneratedValue Long id;

    @Column(nullable = false, length = 256, unique = true)
    private String name;

    @Column(nullable = false, length = 1000000)
    private String xml;

    public ExternalInterfaceXml( String name, String xml )
    {
        this.name = name;
        this.xml = xml;
    }
}
