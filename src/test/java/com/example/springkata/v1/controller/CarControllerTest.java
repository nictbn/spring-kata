package com.example.springkata.v1.controller;

import static com.example.springkata.v1.util.CarProvider.createDefaultCar;
import static com.example.springkata.v1.util.ControllerPaths.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.springkata.v1.domain.Car;
import com.example.springkata.v1.exception.CarNotFoundException;
import com.example.springkata.v1.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CarController.class)
class CarControllerTest {
    public static final String VALID_ID = "1";
    public static final String INVALID_ID = "-1";
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CarService carService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void whenCarsDoNotExist_responseStatusIsNotFound() throws Exception {
        mockMvc.perform(get(GET_CARS_V1_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenFindAllIsCalled_andCarsExist_responseStatusIsOk() throws Exception {
        Car car = createDefaultCar();
        when(carService.findAll(any())).thenReturn(List.of(car));
        mockMvc.perform(get(GET_CARS_V1_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenCarsExist_theyAreReturnedInTheResponseBody() throws Exception {
        Car car = createDefaultCar();
        when(carService.findAll(any())).thenReturn(List.of(car));
        mockMvc.perform(get(GET_CARS_V1_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void whenMakeIsSpecified_onlyCarsOfThatMakeAreReturned() throws Exception {
        Car car = createDefaultCar();
        when(carService.findAll(any())).thenReturn(List.of(car));
        mockMvc.perform(get(GET_CARS_V1_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void whenCarDoesNotExist_notFoundStatusCodeIsReturned() throws Exception {
        mockMvc.perform(get(GET_CAR_BY_ID_V1, INVALID_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenExistingCarIsSelectedById_statusCodeIsOk() throws Exception {
        when(carService.findById(any())).thenReturn(createDefaultCar());

        mockMvc.perform(get(GET_CAR_BY_ID_V1, VALID_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenExistingCarIsSelectedById_responseBodyIsPopulated() throws Exception {
        when(carService.findById(any())).thenReturn(createDefaultCar());

        mockMvc.perform(get(GET_CAR_BY_ID_V1, VALID_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.make", is(createDefaultCar().getMake())));
    }

    @Test
    void whenPostEndpointIsCalledEmptyBody_responseIsBadRequest() throws Exception {
        mockMvc.perform(post(POST_CAR_V1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPostEndpointIsCalledWithCorrectBody_responseIsCreated() throws Exception {
        String carPayload = objectMapper.writeValueAsString(createDefaultCar());
        when(carService.save(any())).thenReturn(createDefaultCar());
        mockMvc.perform(post(POST_CAR_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carPayload))
                .andExpect(status().isCreated());
    }

    @Test
    void whenCarIsNotPersisted_internalServerErrorIsReturned() throws Exception {
        String carPayload = objectMapper.writeValueAsString(createDefaultCar());

        when(carService.save(any())).thenReturn(null);

        mockMvc.perform(post(POST_CAR_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carPayload))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenPostIsSuccessful_responseContainsLocationHeader() throws Exception {
        Car car = createDefaultCar();
        car.setId(1L);
        String carPayload = objectMapper.writeValueAsString(car);
        when(carService.save(any())).thenReturn(car);
        mockMvc.perform(post(POST_CAR_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carPayload))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION, is(GET_CAR_WITHOUT_ID_V1 + car.getId())));
    }

    @Test
    void whenDeleteEndpointIsCalled_itReturnsNoContent() throws Exception {
        mockMvc.perform(delete(DELETE_CAR_BY_ID_V1, VALID_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteEndpointIsCalled_deleteMethodIsCalledOnService() throws Exception {
        mockMvc.perform(delete(DELETE_CAR_BY_ID_V1, VALID_ID))
                .andExpect(status().isNoContent());

        verify(carService, times(1)).deleteById(any());
    }

    @Test
    void whenCarToBeUpdatedIsNotFound_notFoundStatusCodeIsReturned() throws Exception {
        when(carService.updateById(any(), any())).thenThrow(new CarNotFoundException("Could not find car with id -1"));

        mockMvc.perform(put(PUT_CAR_BY_ID_V1, INVALID_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDefaultCar())))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCarToBeUpdatedIsFound_noContentStatusCodeIsReturned() throws Exception {
        Car car = createDefaultCar();
        car.setId(1L);
        when(carService.updateById(any(), any())).thenReturn(car);

        mockMvc.perform(put(PUT_CAR_BY_ID_V1, VALID_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDefaultCar())))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenCarToBeUpdatedIsFound_locationHeaderIsReturned() throws Exception {
        Car car = createDefaultCar();
        car.setId(1L);
        when(carService.updateById(any(), any())).thenReturn(car);

        mockMvc.perform(put(PUT_CAR_BY_ID_V1, VALID_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDefaultCar())))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.LOCATION, is(GET_CAR_WITHOUT_ID_V1 + VALID_ID)));
    }

}