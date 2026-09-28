package driver_vehicle_service.service;

import driver_vehicle_service.model.Driver;
import driver_vehicle_service.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // Create a new driver
    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    // Get all drivers
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // Get driver by ID
    public Optional<Driver> getDriverById(String id) {
        return driverRepository.findById(id);
    }

    // Update driver
    public Driver updateDriver(String id, Driver driver) {
        driver.setId(id);
        return driverRepository.save(driver);
    }

    // Delete driver
    public void deleteDriver(String id) {
        driverRepository.deleteById(id);
    }

    public List<Driver> getEligibleDrivers(String serviceArea) {
        return driverRepository.findByStatusAndServiceArea("AVAILABLE", serviceArea);
    }
}