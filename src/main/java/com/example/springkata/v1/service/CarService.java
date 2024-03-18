package com.example.springkata.v1.service;

import java.util.List;

import com.example.springkata.v1.domain.Car;

public interface CarService {
    List<Car> findAll(String make);

    Car findById(Long carId);

    Car save(Car car);

    void deleteById(Long id);

    Car updateById(Long carId, Car car);
}
