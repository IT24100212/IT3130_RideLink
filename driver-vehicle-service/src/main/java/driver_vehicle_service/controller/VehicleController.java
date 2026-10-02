package driver_vehicle_service.controller;

import driver_vehicle_service.model.Vehicle;
import driver_vehicle_service.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(
        name = "Vehicle Management",
        description = "APIs for managing vehicle details and vehicle status"
)
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(
            summary = "Create a new vehicle",
            description = "Creates and stores a new vehicle with its details and availability status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle data")
    })
    @PostMapping
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.createVehicle(vehicle);
    }

    @Operation(
            summary = "Get all vehicles",
            description = "Retrieves all registered vehicles."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Vehicles retrieved successfully"
    )
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @Operation(
            summary = "Get vehicle by ID",
            description = "Retrieves a specific vehicle using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(
            @Parameter(
                    description = "Unique ID of the vehicle",
                    example = "6ab9ff933eae7f2428c13b85"
            )
            @PathVariable String id) {

        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update vehicle",
            description = "Updates an existing vehicle's details and status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(
            @Parameter(
                    description = "Unique ID of the vehicle",
                    example = "6ab9ff933eae7f2428c13b85"
            )
            @PathVariable String id,
            @RequestBody Vehicle vehicle) {

        if (vehicleService.getVehicleById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                vehicleService.updateVehicle(id, vehicle)
        );
    }

    @Operation(
            summary = "Delete vehicle",
            description = "Deletes a vehicle from the system using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(
                    description = "Unique ID of the vehicle",
                    example = "6ab9ff933eae7f2428c13b85"
            )
            @PathVariable String id) {

        if (vehicleService.getVehicleById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}