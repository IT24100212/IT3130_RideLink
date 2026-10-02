package driver_vehicle_service.controller;

import driver_vehicle_service.model.Driver;
import driver_vehicle_service.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@Tag(
        name = "Driver Management",
        description = "APIs for managing driver profiles, availability, service areas, and simulated locations"
)
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @Operation(
            summary = "Create a new driver",
            description = "Creates and stores a new driver operational profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid driver data")
    })
    @PostMapping
    public Driver createDriver(@RequestBody Driver driver) {
        return driverService.createDriver(driver);
    }

    @Operation(
            summary = "Get all drivers",
            description = "Retrieves all registered driver operational profiles."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Drivers retrieved successfully"
    )
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @Operation(
            summary = "Get eligible available drivers",
            description = "Retrieves drivers who are available and operate within the specified service area."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eligible drivers retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Service area is missing or invalid")
    })
    @GetMapping("/eligible")
    public List<Driver> getEligibleDrivers(
            @Parameter(
                    description = "Service area used to filter eligible drivers",
                    example = "Colombo"
            )
            @RequestParam String serviceArea) {

        return driverService.getEligibleDrivers(serviceArea);
    }

    @Operation(
            summary = "Get driver by ID",
            description = "Retrieves a specific driver using the driver's unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver found"),
            @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(
            @Parameter(
                    description = "Unique ID of the driver",
                    example = "6ab9f28a35e5d0b2b195e674"
            )
            @PathVariable String id) {

        return driverService.getDriverById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update driver",
            description = "Updates an existing driver's operational profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver updated successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(
            @Parameter(
                    description = "Unique ID of the driver",
                    example = "6ab9f28a35e5d0b2b195e674"
            )
            @PathVariable String id,

            @RequestBody Driver driver) {

        if (driverService.getDriverById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(driverService.updateDriver(id, driver));
    }

    @Operation(
            summary = "Delete driver",
            description = "Deletes a driver from the system using the driver's unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Driver deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(
            @Parameter(
                    description = "Unique ID of the driver",
                    example = "6ab9f28a35e5d0b2b195e674"
            )
            @PathVariable String id) {

        if (driverService.getDriverById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}