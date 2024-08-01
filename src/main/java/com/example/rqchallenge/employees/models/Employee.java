package com.example.rqchallenge.employees.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private int id;
    private String employeeName;
    private int employeeSalary;
    private int employeeAge;
    private String profileImage;
}
