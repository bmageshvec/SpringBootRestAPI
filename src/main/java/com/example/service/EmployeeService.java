package com.example.service;

import com.example.dao.DepartmentDAO;
import com.example.dao.EmployeeDAO;
import com.example.dto.EmployeeDTO;
import com.example.entity.Department;
import com.example.entity.Employee;
import com.example.mapper.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeDAO employeeDAO;

    @Autowired
    private DepartmentDAO departmentDAO;  // Add DepartmentDAO

    @Autowired
    private EntityMapper mapper;

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = mapper.toEmployeeEntity(employeeDTO);
        if (employeeDTO.getDepartmentId() != null) {
            // Check if department exists, if not create it
            Department department = departmentDAO.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(department);
        }
        return mapper.toEmployeeDTO(employeeDAO.save(employee));
    }

    public EmployeeDTO getEmployee(Long id) {
        return mapper.toEmployeeDTO(employeeDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found")));
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeDAO.findAll().stream()
                .map(mapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = mapper.toEmployeeEntity(employeeDTO);
        if (employeeDTO.getDepartmentId() != null) {
            Department department = departmentDAO.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(department);
        }
        return mapper.toEmployeeDTO(employeeDAO.save(employee));
    }

    public void deleteEmployee(Long id) {
        employeeDAO.deleteById(id);
    }
}