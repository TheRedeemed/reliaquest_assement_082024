package com.example.rqchallenge.employees.controller;

import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController implements IEmployeeController {
    private final EmployeeService employeeService;

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException, URISyntaxException {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString)
            throws URISyntaxException, IOException {
        return ResponseEntity.ok(employeeService.getEmployeesByName(searchString));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) throws URISyntaxException, IOException {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Override
    @GetMapping("/highest-salary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() throws URISyntaxException, IOException {
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    @GetMapping("/top-ten-highest-earning-employee-names")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() throws URISyntaxException, IOException {
        return new ResponseEntity<>(employeeService.getTop10HighestEarningEmployeeNames(), HttpStatus.OK);
    }

    @Override
    @PostMapping
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) throws URISyntaxException, IOException {
        return new ResponseEntity<>(employeeService.createEmployee(employeeInput), HttpStatus.CREATED);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(String id) throws URISyntaxException, IOException {
        return new ResponseEntity<>(employeeService.deleteEmployeeById(id), HttpStatus.OK);
    }
}
