package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.*;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.app.quantitymeasurement.config.SecurityConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuantityMeasurementController.class)
@Import(SecurityConfig.class)
class QuantityMeasurementControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean IQuantityMeasurementService service;

    private QuantityInputDTO buildInput(String v1, String u1, String t1,
                                        String v2, String u2, String t2) {
        QuantityDTO q1 = new QuantityDTO(Double.parseDouble(v1), u1, t1);
        QuantityDTO q2 = new QuantityDTO(Double.parseDouble(v2), u2, t2);
        return new QuantityInputDTO(q1, q2);
    }

    private QuantityMeasurementDTO successDTO(String op) {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setOperation(op);
        dto.setError(false);
        return dto;
    }

    @Test
    void testCompare_Returns200() throws Exception {
        when(service.compareEquality(any(), any())).thenReturn(successDTO("compare"));
        mockMvc.perform(post("/api/v1/quantities/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildInput("1.0","FEET","LengthUnit",
                                        "12.0","INCHES","LengthUnit"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("compare"));
    }

    @Test
    void testConvert_Returns200() throws Exception {
        QuantityMeasurementDTO dto = successDTO("convert");
        dto.setResultValue(12.0);
        when(service.convert(any(), any())).thenReturn(dto);
        mockMvc.perform(post("/api/v1/quantities/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildInput("1.0","FEET","LengthUnit",
                                        "0.0","INCHES","LengthUnit"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultValue").value(12.0));
    }

    @Test
    void testAdd_Returns200() throws Exception {
        QuantityMeasurementDTO dto = successDTO("add");
        dto.setResultValue(2.0);
        dto.setResultUnit("FEET");
        when(service.add(any(), any())).thenReturn(dto);
        mockMvc.perform(post("/api/v1/quantities/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildInput("1.0","FEET","LengthUnit",
                                        "12.0","INCHES","LengthUnit"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultValue").value(2.0));
    }

    @Test
    void testSubtract_Returns200() throws Exception {
        QuantityMeasurementDTO dto = successDTO("subtract");
        dto.setResultValue(9.5);
        when(service.subtract(any(), any())).thenReturn(dto);
        mockMvc.perform(post("/api/v1/quantities/subtract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildInput("10.0","FEET","LengthUnit",
                                        "6.0","INCHES","LengthUnit"))))
                .andExpect(status().isOk());
    }

    @Test
    void testDivide_Returns200() throws Exception {
        QuantityMeasurementDTO dto = successDTO("divide");
        dto.setResultValue(5.0);
        when(service.divide(any(), any())).thenReturn(dto);
        mockMvc.perform(post("/api/v1/quantities/divide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildInput("10.0","FEET","LengthUnit",
                                        "2.0","FEET","LengthUnit"))))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidInput_Returns400() throws Exception {
        // Empty unit — fails @NotEmpty validation
        String badBody = """
            {"thisQuantityDTO":{"value":1.0,"unit":"","measurementType":"LengthUnit"},
             "thatQuantityDTO":{"value":12.0,"unit":"INCHES","measurementType":"LengthUnit"}}
            """;
        mockMvc.perform(post("/api/v1/quantities/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHistoryByOperation_Returns200() throws Exception {
        when(service.getHistoryByOperation("COMPARE"))
                .thenReturn(java.util.List.of(successDTO("compare")));
        mockMvc.perform(get("/api/v1/quantities/history/operation/COMPARE"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetOperationCount_Returns200() throws Exception {
        when(service.getOperationCount("COMPARE")).thenReturn(3L);
        mockMvc.perform(get("/api/v1/quantities/count/COMPARE"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void testGetErrorHistory_Returns200() throws Exception {
        when(service.getErrorHistory()).thenReturn(java.util.List.of());
        mockMvc.perform(get("/api/v1/quantities/history/errored"))
                .andExpect(status().isOk());
    }
}