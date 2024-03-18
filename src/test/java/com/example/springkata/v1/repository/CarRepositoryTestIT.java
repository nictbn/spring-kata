package com.example.springkata.v1.repository;

import static com.example.springkata.v1.util.CarProvider.createDefaultCar;
import static com.example.springkata.v1.util.MakeVerifier.failTestIfUnexpectedMake;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.springkata.bootstrap.CarInitializer;
import com.example.springkata.v1.domain.Car;

@DataJpaTest
@Import(CarInitializer.class)
class CarRepositoryTestIT {
    @Autowired
    CarRepository repository;

    @Test
    void whenApplicationStarts_databaseIsPopulated() {
        assertEquals(3, repository.count());
    }

    @Test
    void whenCarIsInserted_idIsNotNull() {
        Car car = createDefaultCar();
        Car persistedCar = repository.save(car);
        assertNotNull(persistedCar.getId());
    }

    @Test
    void whenCarMakeDoesNotExist_resultIsEmpty() {
        List<Car> fordCars = repository.findByMakeIgnoreCase("Ford");
        assertEquals(0, fordCars.size());
    }

    @Test
    void whenCarMakeExist_onlyCarsOfThatMakeAreReturned() {
        String fiat = "Fiat";
        List<Car> cars = repository.findByMakeIgnoreCase(fiat);
        assertFalse(cars.isEmpty());
        failTestIfUnexpectedMake(cars, fiat);
    }
}