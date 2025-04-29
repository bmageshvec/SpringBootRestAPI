package com.example.controller;

import com.example.dto.EmployeeDTO;
import com.example.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeController.
 * Tests REST endpoints and their responses.
 */
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeDTO = new EmployeeDTO();
        employeeDTO.setId(1L);
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Doe");
        employeeDTO.setEmail("john.doe@company.com");
        employeeDTO.setDepartmentId(1L);
    }

    /**
     * Tests creating an employee via POST endpoint.
     */
    @Test
    void createEmployee_success() {
        when(employeeService.createEmployee(employeeDTO)).thenReturn(employeeDTO);

        ResponseEntity<EmployeeDTO> response = employeeController.createEmployee(employeeDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(employeeDTO, response.getBody());
        verify(employeeService, times(1)).createEmployee(employeeDTO);
    }

    /**
     * Tests getting an employee by ID via GET endpoint.
     */
    @Test
    void getEmployee_success() {
        when(employeeService.getEmployee(1L)).thenReturn(employeeDTO);

        ResponseEntity<EmployeeDTO> response = employeeController.getEmployee(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeDTO, response.getBody());
        verify(employeeService, times(1)).getEmployee(1L);
    }

    /**
     * Tests getting all employees via GET endpoint.
     */
    @Test
    void getAllEmployees_success() {
        List<EmployeeDTO> employeeDTOs = Arrays.asList(employeeDTO);
        when(employeeService.getAllEmployees()).thenReturn(employeeDTOs);

        ResponseEntity<List<EmployeeDTO>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeDTOs, response.getBody());
        assertEquals(1, response.getBody().size());
        verify(employeeService, times(1)).getAllEmployees();
    }

    /**
     * Tests updating an employee via PUT endpoint.
     */
    @Test
    void updateEmployee_success() {
        when(employeeService.updateEmployee(employeeDTO)).thenReturn(employeeDTO);

        ResponseEntity<EmployeeDTO> response = employeeController.updateEmployee(1L, employeeDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeDTO, response.getBody());
        assertEquals(1L, employeeDTO.getId()); // Verify ID was set from path variable
        verify(employeeService, times(1)).updateEmployee(employeeDTO);
    }

    /**
     * Tests deleting an employee via DELETE endpoint.
     */
    @Test
    void deleteEmployee_success() {
        doNothing().when(employeeService).deleteEmployee(1L);

        ResponseEntity<Void> response = employeeController.deleteEmployee(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(employeeService, times(1)).deleteEmployee(1L);
    }
}