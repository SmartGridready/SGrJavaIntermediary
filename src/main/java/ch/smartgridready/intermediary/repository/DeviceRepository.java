package ch.smartgridready.intermediary.repository;

import ch.smartgridready.intermediary.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByName(String name);
}
