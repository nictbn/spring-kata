package com.example.springkata.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springkata.v1.domain.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByMakeIgnoreCase(String make);
}
