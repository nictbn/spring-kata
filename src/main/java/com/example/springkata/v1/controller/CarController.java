package com.example.springkata.v1.controller;

import static com.example.springkata.v1.util.ControllerPaths.*;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.springkata.v1.domain.Car;
import com.example.springkata.v1.exception.CarNotFoundException;
import com.example.springkata.v1.service.CarService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping(GET_CARS_V1_PATH)
    ResponseEntity<List<Car>> findCars(@RequestParam(required = false, value = "make") String make) {
        List<Car> cars = carService.findAll(make);
        if (cars.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping(GET_CAR_BY_ID_V1)
    ResponseEntity<Car> findCar(@PathVariable(required = true, value = "carId") Long carId) {
        Car car = carService.findById(carId);
        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @PostMapping(POST_CAR_V1)
    ResponseEntity<Void> createCar(@RequestBody @Validated Car car) {
        Car persistedCar = carService.save(car);
        if (persistedCar == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders headers = getHttpHeaders(persistedCar.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping(DELETE_CAR_BY_ID_V1)
    ResponseEntity<Void> deleteById(@PathVariable("carId") Long carId) {
        carService.deleteById(carId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(PUT_CAR_BY_ID_V1)
    ResponseEntity<Void> updateById(@PathVariable("carId") Long carId, @RequestBody @Validated Car car) {
        Car car1 = carService.updateById(carId, car);
        HttpHeaders httpHeaders = getHttpHeaders(car1.getId());
        return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
    }



    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<Void> handleCarNotFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private HttpHeaders getHttpHeaders(Long carId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, GET_CAR_WITHOUT_ID_V1 + carId);
        return headers;
    }
}
