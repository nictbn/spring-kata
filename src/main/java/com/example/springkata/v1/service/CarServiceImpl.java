package com.example.springkata.v1.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.springkata.v1.domain.Car;
import com.example.springkata.v1.exception.CarNotFoundException;
import com.example.springkata.v1.repository.CarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    @Override
    public List<Car> findAll(String make) {
        List<Car> result;
        if (make == null) {
            result = carRepository.findAll();
        } else {
            result = carRepository.findByMakeIgnoreCase(make);
        }
        return result;
    }

    @Override
    public Car findById(Long carId) {
        Assert.notNull(carId, "Car id cannot be null when searching by id!");
        return carRepository.findById(carId).orElse(null);
    }

    @Override
    public Car save(Car car) {
        Assert.notNull(car, "Car cannot be null when persisting!");
        Car dto = Car.builder()
                .make(car.getMake())
                .model(car.getModel())
                .build();
        return carRepository.save(dto);
    }

    @Override
    public void deleteById(Long id) {
        Assert.notNull(id, "Car id cannot be null when deleting by id!");
        carRepository.deleteById(id);
    }

    @Override
    public Car updateById(Long carId, Car car) {
        Assert.notNull(carId, "Car id cannot be null when updating!");
        Assert.notNull(car, "Car cannot be null when updating!");
        Car existingCar = carRepository.findById(carId).orElseThrow(() -> new CarNotFoundException("Could not find car with id: " + carId));
        existingCar.setMake(car.getMake());
        existingCar.setModel(car.getModel());
        return carRepository.save(existingCar);
    }
}
