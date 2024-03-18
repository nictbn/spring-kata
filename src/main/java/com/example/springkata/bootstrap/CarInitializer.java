package com.example.springkata.bootstrap;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.springkata.v1.domain.Car;
import com.example.springkata.v1.repository.CarRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CarInitializer implements CommandLineRunner {
    private final CarRepository repository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing database!");
        if (repository.count() > 0) {
            System.out.println("Database is already initialized!");
            return;
        }
        Car toyota = Car.builder()
                .make("Toyota")
                .model("Hilux")
                .build();
        Car volkswagen = Car.builder()
                .make("Volkswagen")
                .model("Beetle")
                .build();
        Car fiat = Car.builder()
                .make("Fiat")
                .model("Panda")
                .build();
        repository.saveAll(List.of(toyota, volkswagen, fiat));
        System.out.println("Database initialized successfully!");
    }
}
