package com.smartgridready.intermediary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartgridready.intermediary.entity.ExternalInterfaceXml;

import java.util.List;


/**
 * Repository for EI-XML. 
 */
public interface ExternalInterfaceXmlRepository extends JpaRepository<ExternalInterfaceXml, Long>
{

    List<ExternalInterfaceXml> findByName( String name );
}