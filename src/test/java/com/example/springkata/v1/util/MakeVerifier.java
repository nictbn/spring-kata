package com.example.springkata.v1.util;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import com.example.springkata.v1.domain.Car;

public class MakeVerifier {
    public static void failTestIfUnexpectedMake(List<Car> cars, String make) {
        for (Car car : cars) {
            if (!make.equalsIgnoreCase(car.getMake())) {
                fail();
            }
        }
    }
}
