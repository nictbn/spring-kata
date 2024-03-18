package com.example.springkata.v1.service;

import static com.example.springkata.v1.util.CarProvider.createDefaultCar;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.springkata.v1.domain.Car;
import com.example.springkata.v1.exception.CarNotFoundException;
import com.example.springkata.v1.repository.CarRepository;


class CarServiceImplTest {
    public static final String CAR_MAKE = "CAR_MAKE";
    @Mock
    CarRepository repository;
    CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carService = new CarServiceImpl(repository);
    }

    @Test
    void whenCarMakeIsNull_allCarsAreReturned() {
        List<Car> cars = carService.findAll(null);
        assertEquals(0, cars.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void whenCarMakeIsNotNull_carsAreFiltered() {
        List<Car> cars = carService.findAll(CAR_MAKE);
        assertEquals(0, cars.size());
        verify(repository, times(1)).findByMakeIgnoreCase(anyString());
    }

    @Test
    void whenCarIdIsNull_exceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            carService.findById(null);
        });
    }

    @Test
    void whenCarIsNotFoundById_nullIsReturned() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        Car car = carService.findById(1L);
        assertNull(car);
    }

    @Test
    void whenPersistingNullCar_exceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            carService.save(null);
        });
    }

    @Test
    void whenPersistingCar_repositorySaveMethodIsCalled() {
        carService.save(createDefaultCar());
        verify(repository, times(1)).save(any());
    }

    @Test
    void whenDeletingNullCar_exceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            carService.deleteById(null);
        });
    }

    @Test
    void whenDeletingCar_repositoryDeleteMethodIsCalled() {
        carService.deleteById(1L);
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    void whenCarToBeUpdatedIsNotFound_exceptionIsThrown() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(CarNotFoundException.class, () -> {
            carService.updateById(Long.MIN_VALUE, createDefaultCar());
        });
    }

    @Test
    void whenCarToBeUpdatedIsNotFound_nothingIsSaved() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(CarNotFoundException.class, () -> {
            carService.updateById(Long.MIN_VALUE, createDefaultCar());
        });
        verify(repository, times(0)).save(any());
    }

    @Test
    void whenCarToBeUpdatedIsFound_saveMethodIsCalled() {
        when(repository.findById(any())).thenReturn(Optional.of(createDefaultCar()));
        carService.updateById(Long.MIN_VALUE, createDefaultCar());
        verify(repository, times(1)).save(any());
    }
}