package com.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentDTO {

    private Long id;
    private String name;
    private String location;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EmployeeDTO> employees;

}