package driver_vehicle_service.service;

import driver_vehicle_service.model.Driver;
import driver_vehicle_service.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDriver_ShouldReturnSavedDriver() {
        Driver driver = new Driver();
        driver.setName("John Doe");
        
        when(driverRepository.save(driver)).thenReturn(driver);

        Driver savedDriver = driverService.createDriver(driver);

        assertNotNull(savedDriver);
        assertEquals("John Doe", savedDriver.getName());
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void getEligibleDrivers_ShouldReturnAvailableDriversInArea() {
        Driver driver = new Driver();
        driver.setStatus("AVAILABLE");
        driver.setServiceArea("Colombo");

        when(driverRepository.findByStatusAndServiceArea("AVAILABLE", "Colombo"))
                .thenReturn(Arrays.asList(driver));

        List<Driver> drivers = driverService.getEligibleDrivers("Colombo");

        assertFalse(drivers.isEmpty());
        assertEquals(1, drivers.size());
        assertEquals("Colombo", drivers.get(0).getServiceArea());
    }

    @Test
    void getDriverById_WhenFound_ShouldReturnDriver() {
        Driver driver = new Driver();
        driver.setId("123");
        
        when(driverRepository.findById("123")).thenReturn(Optional.of(driver));

        Optional<Driver> foundDriver = driverService.getDriverById("123");

        assertTrue(foundDriver.isPresent());
        assertEquals("123", foundDriver.get().getId());
    }
}
