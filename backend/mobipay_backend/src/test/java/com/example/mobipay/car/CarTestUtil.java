package com.example.mobipay.car;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.mobipay.domain.car.dto.CarRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class CarTestUtil {

    public static String createCarRegisterRequest(ObjectMapper objectMapper, String carNumber, String carModel)
            throws Exception {
        CarRegisterRequest carRegisterRequest = new CarRegisterRequest(carNumber, carModel);
        return objectMapper.writeValueAsString(carRegisterRequest);
    }

    public static ResultActions performCarRegistration(MockMvc mockMvc, ObjectMapper objectMapper, String carNumber,
                                                       String carModel)
            throws Exception {
        String requestBody = createCarRegisterRequest(objectMapper, carNumber, carModel);
        return mockMvc.perform(post("/api/v1/cars")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));
    }
}