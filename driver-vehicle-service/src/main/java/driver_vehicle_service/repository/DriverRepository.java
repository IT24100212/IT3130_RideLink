package driver_vehicle_service.repository;

import driver_vehicle_service.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DriverRepository extends MongoRepository<Driver, String> {

    List<Driver> findByStatusAndServiceArea(String status, String serviceArea);
}