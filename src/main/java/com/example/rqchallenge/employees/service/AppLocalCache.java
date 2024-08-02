package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.models.Employee;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This cache will be used to cache employee data
 * As an improvement, a Redis cache could be implemented to replace this Local Cache
 */
@Component
public class AppLocalCache {
    Map<String, Employee> employeeByIdCache = new HashMap<>();

    public void addToEmployeeCacheById(String employeeId, Employee employee) {
        employeeByIdCache.put(employeeId, employee);
    }

    public Employee getFromEmployeeCacheById(String employeeById) {
        return employeeByIdCache.get(employeeById);
    }

    public void removeFromEmployeeCacheById(String employeeId) {
        employeeByIdCache.remove(employeeId);
    }
}
