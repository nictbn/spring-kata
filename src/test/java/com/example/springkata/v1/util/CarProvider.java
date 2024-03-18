package com.example.springkata.v1.util;

import com.example.springkata.v1.domain.Car;

public class CarProvider {
    public static Car createDefaultCar() {
        return Car.builder()
                .make("Toyota")
                .model("Hilux")
                .build();
    }

    public static Car createCustomCar(String make, String model) {
        return Car.builder()
                .make(make)
                .model(model)
                .build();
    }
}
