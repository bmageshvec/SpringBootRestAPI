package com.example.service;

import com.example.dao.DepartmentDAO;
import com.example.dto.DepartmentDTO;
import com.example.entity.Department;
import com.example.mapper.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentDAO departmentDAO;
    @Autowired
    private EntityMapper mapper;

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Department department = mapper.toDepartmentEntity(departmentDTO);
        return mapper.toDepartmentDTO(departmentDAO.save(department));
    }

    public DepartmentDTO getDepartment(Long id) {
        return mapper.toDepartmentDTO(departmentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found")));
    }

    public List<DepartmentDTO> getAllDepartments() {
        return departmentDAO.findAll().stream()
                .map(mapper::toDepartmentDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO) {
        Department department = mapper.toDepartmentEntity(departmentDTO);
        return mapper.toDepartmentDTO(departmentDAO.save(department));
    }

    public void deleteDepartment(Long id) {
        departmentDAO.deleteById(id);
    }
}