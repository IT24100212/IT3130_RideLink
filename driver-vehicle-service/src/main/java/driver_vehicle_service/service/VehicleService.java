package driver_vehicle_service.service;

import driver_vehicle_service.model.Vehicle;
import driver_vehicle_service.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // Create a new vehicle
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    // Get all vehicles
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // Get vehicle by ID
    public Optional<Vehicle> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    // Update vehicle
    public Vehicle updateVehicle(String id, Vehicle vehicle) {
        vehicle.setId(id);
        return vehicleRepository.save(vehicle);
    }

    // Delete vehicle
    public void deleteVehicle(String id) {
        vehicleRepository.deleteById(id);
    }
}