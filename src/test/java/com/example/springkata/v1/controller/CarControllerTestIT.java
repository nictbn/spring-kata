package com.example.springkata.v1.controller;

import static com.example.springkata.v1.util.CarProvider.createCustomCar;
import static com.example.springkata.v1.util.ControllerPaths.*;
import static com.example.springkata.v1.util.MakeVerifier.failTestIfUnexpectedMake;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.springkata.v1.domain.Car;
import com.example.springkata.v1.repository.CarRepository;
import com.example.springkata.v1.util.ControllerPaths;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class CarControllerTestIT {
    public static final String INVALID_ID = "-1";
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CarRepository carRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void whenNoMakeIsSpecified_allCarsAreReturned() throws Exception {
        mockMvc.perform(get(ControllerPaths.GET_CARS_V1_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3)));
    }

    @Test
    void whenMakeIsSpecified_onlyCarsOfThatMakeAreSpecified() throws Exception {
        String make = "Fiat";
        MvcResult mvcResult = mockMvc.perform(get(ControllerPaths.GET_CARS_V1_PATH)
                        .queryParam("make", make)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<Car> cars = objectMapper.readValue(contentAsString, new TypeReference<List<Car>>() {
        });
        failTestIfUnexpectedMake(cars, make);
    }

    @Test
    void whenCarIdExists_itIsReturned() throws Exception {
        Car car = carRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(car);

        mockMvc.perform(get(GET_CAR_BY_ID_V1, car.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(car.getId()), Long.class))
                .andExpect(jsonPath("$.make", is(car.getMake())))
                .andExpect(jsonPath("$.model", is(car.getModel())));
    }

    @Test
    void whenCarIdDoesNotExist_notFoundIsReturned() throws Exception {
        Car car = carRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(car);

        mockMvc.perform(get(GET_CAR_BY_ID_V1, INVALID_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void whenPostIsSuccessful_carIsPersisted() throws Exception {
        Car subaru = createCustomCar("Subaru", "Imprezza");

        MvcResult postResult = mockMvc.perform(post(POST_CAR_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subaru)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andReturn();
        String locationHeader = postResult.getResponse().getHeader(HttpHeaders.LOCATION);
        assertNotNull(locationHeader);

        MvcResult getResult = mockMvc.perform(get(locationHeader)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseBody = getResult.getResponse().getContentAsString();
        Car returnedCar = objectMapper.readValue(responseBody, Car.class);
        assertEquals(subaru.getMake(), returnedCar.getMake());
        assertEquals(subaru.getModel(), returnedCar.getModel());
        assertNotNull(returnedCar.getId());
        assertNotNull(returnedCar.getCreatedAt());
        assertNotNull(returnedCar.getCreatedAt());
    }

    @Test
    void carIsDeletedSuccessfully() throws Exception {
        Car honda = createCustomCar("Honda", "Civic");

        MvcResult postResult = mockMvc.perform(post(POST_CAR_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(honda)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andReturn();
        String locationHeader = postResult.getResponse().getHeader(HttpHeaders.LOCATION);
        Long id = Long.parseLong(locationHeader.substring(locationHeader.lastIndexOf("/") + 1));

        mockMvc.perform(delete(DELETE_CAR_BY_ID_V1, id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(locationHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    void carIsUpdatedSuccessfully() throws Exception {
        Car honda = createCustomCar("Honda", "Civic");

        MvcResult postResult = mockMvc.perform(post(POST_CAR_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(honda)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andReturn();
        String locationHeader = postResult.getResponse().getHeader(HttpHeaders.LOCATION);
        Long id = Long.parseLong(locationHeader.substring(locationHeader.lastIndexOf("/") + 1));

        Car newCar = createCustomCar("Nissan", "Qashqai");
        mockMvc.perform(put(PUT_CAR_BY_ID_V1, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(locationHeader)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make", is(newCar.getMake())))
                .andExpect(jsonPath("$.model", is(newCar.getModel())));
    }
}
