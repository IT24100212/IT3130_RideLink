package driver_vehicle_service.repository;

import driver_vehicle_service.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DriverRepository extends MongoRepository<Driver, String> {
}