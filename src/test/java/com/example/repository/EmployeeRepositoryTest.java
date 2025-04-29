package com.example.repository;

import com.example.entity.Department;
import com.example.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmployeeRepository.
 * Tests basic CRUD operations using H2 in-memory database with test profile.
 * No data.sql is executed; data is set up in tests.
 */
@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;  // Add DepartmentRepository

    private Department department;
    private Employee employee;

    @BeforeEach
    void setUp() {
        // Setup test data and save Department first
        department = new Department(null, "IT", "Building A", null);
        department = departmentRepository.save(department);  // Save Department first
        employee = new Employee(null, "John", "Doe", "john.doe@company.com", department);
    }

    @Test
    void saveEmployee_success() {
        Employee savedEmployee = employeeRepository.save(employee);

        assertNotNull(savedEmployee.getId());
        assertEquals("John", savedEmployee.getFirstName());
        assertEquals("Doe", savedEmployee.getLastName());
        assertEquals("john.doe@company.com", savedEmployee.getEmail());
        assertEquals(department, savedEmployee.getDepartment());
    }

    @Test
    void findById_success() {
        Employee savedEmployee = employeeRepository.save(employee);
        Optional<Employee> foundEmployee = employeeRepository.findById(savedEmployee.getId());

        assertTrue(foundEmployee.isPresent());
        assertEquals(savedEmployee.getId(), foundEmployee.get().getId());
        assertEquals("John", foundEmployee.get().getFirstName());
    }

    @Test
    void findAll_success() {
        employeeRepository.save(employee);
        Employee employee2 = new Employee(null, "Jane", "Smith", "jane.smith@company.com", department);
        employeeRepository.save(employee2);

        List<Employee> employees = employeeRepository.findAll();

        assertEquals(2, employees.size());
        assertTrue(employees.stream().anyMatch(e -> e.getFirstName().equals("John")));
        assertTrue(employees.stream().anyMatch(e -> e.getFirstName().equals("Jane")));
    }

    @Test
    void deleteById_success() {
        Employee savedEmployee = employeeRepository.save(employee);
        employeeRepository.deleteById(savedEmployee.getId());

        Optional<Employee> foundEmployee = employeeRepository.findById(savedEmployee.getId());
        assertFalse(foundEmployee.isPresent());
    }
}