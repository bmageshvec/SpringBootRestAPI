package com.example.service;

import com.example.dao.EmployeeDAO;
import com.example.dto.EmployeeDTO;
import com.example.entity.Department;
import com.example.entity.Employee;
import com.example.mapper.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeService.
 * Tests business logic and interactions with DAO and Mapper layers.
 */
class EmployeeServiceTest {

    @Mock
    private EmployeeDAO employeeDAO;

    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Department department = new Department(1L, "IT", "Building A", null);
        employee = new Employee(1L, "John", "Doe", "john.doe@company.com", department);
        employeeDTO = new EmployeeDTO();
        employeeDTO.setId(1L);
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Doe");
        employeeDTO.setEmail("john.doe@company.com");
        employeeDTO.setDepartmentId(1L);
    }

    /**
     * Tests creating an employee successfully.
     */
    @Test
    void createEmployee_success() {
        when(mapper.toEmployeeEntity(employeeDTO)).thenReturn(employee);
        when(employeeDAO.save(employee)).thenReturn(employee);
        when(mapper.toEmployeeDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(mapper, times(1)).toEmployeeEntity(employeeDTO);
        verify(employeeDAO, times(1)).save(employee);
        verify(mapper, times(1)).toEmployeeDTO(employee);
    }

    /**
     * Tests getting an employee by ID successfully.
     */
    @Test
    void getEmployee_success() {
        when(employeeDAO.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.toEmployeeDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployee(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(employeeDAO, times(1)).findById(1L);
        verify(mapper, times(1)).toEmployeeDTO(employee);
    }

    /**
     * Tests getting an employee that doesn't exist.
     */
    @Test
    void getEmployee_notFound() {
        when(employeeDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.getEmployee(1L));
        verify(employeeDAO, times(1)).findById(1L);
    }

    /**
     * Tests getting all employees successfully.
     */
    @Test
    void getAllEmployees_success() {
        List<Employee> employees = Arrays.asList(employee);
        List<EmployeeDTO> employeeDTOs = Arrays.asList(employeeDTO);
        when(employeeDAO.findAll()).thenReturn(employees);
        when(mapper.toEmployeeDTO(employee)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(employeeDAO, times(1)).findAll();
        verify(mapper, times(1)).toEmployeeDTO(employee);
    }

    /**
     * Tests updating an employee successfully.
     */
    @Test
    void updateEmployee_success() {
        when(mapper.toEmployeeEntity(employeeDTO)).thenReturn(employee);
        when(employeeDAO.save(employee)).thenReturn(employee);
        when(mapper.toEmployeeDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(mapper, times(1)).toEmployeeEntity(employeeDTO);
        verify(employeeDAO, times(1)).save(employee);
        verify(mapper, times(1)).toEmployeeDTO(employee);
    }

    /**
     * Tests deleting an employee successfully.
     */
    @Test
    void deleteEmployee_success() {
        doNothing().when(employeeDAO).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeDAO, times(1)).deleteById(1L);
    }
}