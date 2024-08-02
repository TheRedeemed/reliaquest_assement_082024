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

    /**
     * This should return all employees
     *
     * @return list of employees
     * @throws IOException IOException
     * @throws URISyntaxException URISyntaxException
     */
    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException, URISyntaxException {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /**
     * This should return all employees whose name contains or matches the string input provided
     *
     * @param searchString searchString
     * @return list of employees
     * @throws URISyntaxException URISyntaxException
     * @throws IOException IOException
     */
    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString)
            throws URISyntaxException, IOException {
        return ResponseEntity.ok(employeeService.getEmployeesByName(searchString));
    }

    /**
     * This should return a single employee
     * An EmployeeNotFoundException will be thrown if the user is not found
     *
     * @param id Employee ID
     * @return employee response
     * @throws URISyntaxException URISyntaxException
     * @throws IOException IOException
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) throws URISyntaxException, IOException {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * This should return a single integer indicating the highest salary of all employees
     *
     * @return integer of the highest salary
     * @throws URISyntaxException URISyntaxException
     * @throws IOException IOException
     */
    @Override
    @GetMapping("/highest-salary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() throws URISyntaxException, IOException {
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    /**
     * This should return a list of the top 10 employees based off of their salaries
     *
     * @return list of employees
     * @throws URISyntaxException URISyntaxException
     * @throws IOException IOException
     */
    @Override
    @GetMapping("/top-ten-highest-earning-employee-names")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() throws URISyntaxException, IOException {
        return new ResponseEntity<>(employeeService.getTopTenHighestEarningEmployeeNames(), HttpStatus.OK);
    }

    /**
     * This should return a status of success or failed based on if an employee was created
     *
     * @param employeeInput employee request object
     * @return the employee that was created
     * @throws URISyntaxException URISyntaxException
     * @throws IOException IOException
     */
    @Override
    @PostMapping
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) throws URISyntaxException, IOException {
        return new ResponseEntity<>(employeeService.createEmployee(employeeInput), HttpStatus.CREATED);
    }

    /**
     * This should delete the employee with specified id given
     *
     * @param id Employee ID
     * @return id of the user deleted
     * @throws URISyntaxException URISyntaxException
     * @throws IOException IOException
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(String id) throws URISyntaxException, IOException {
        return new ResponseEntity<>(employeeService.deleteEmployeeById(id), HttpStatus.OK);
    }
}
