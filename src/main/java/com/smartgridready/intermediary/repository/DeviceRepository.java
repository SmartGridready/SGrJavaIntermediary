package com.smartgridready.intermediary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartgridready.intermediary.entity.Device;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByName(String name);
    List<Device> findByEiXmlName(String eiXmlName);
}
