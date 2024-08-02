package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.exception.*;
import com.example.rqchallenge.employees.models.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static com.example.rqchallenge.employees.config.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final CloseableHttpClient closeableHttpClient;
    private final ObjectMapper objectMapper;
    private final AppLocalCache appLocalCache;

    @Value("${employee.api.url}")
    private String employeeApiUrl;


    public List<Employee> getAllEmployees() throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(employeeApiUrl.concat(ALL_EMPLOYEE_PATH));
        HttpGet getRequest = new HttpGet(builder.build());

        log.info("Sending request to get all employees");
        HttpResponse response = closeableHttpClient.execute(getRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseString = EntityUtils.toString(response.getEntity());

        if (statusCode == HttpStatus.OK.value()) {
            try {
                JSONObject jsonResponse = new JSONObject(responseString);
                JSONArray jsonArray = jsonResponse.getJSONArray("data");

                List<Employee> employeesData = new ArrayList<>();

                if (!jsonArray.isEmpty()) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        Employee employee = Employee.builder()
                                .id((jsonObj.getInt("id")))
                                .employeeName(jsonObj.getString("employee_name"))
                                .employeeSalary(jsonObj.getInt("employee_salary"))
                                .employeeAge(jsonObj.getInt("employee_age"))
                                .profileImage(jsonObj.getString("profile_image"))
                                .build();
                        employeesData.add(employee);
                    }
                }
                return employeesData;
            } catch (JSONException e) {
                String errorMessage = e.getMessage();
                log.error("An error occurred {}", errorMessage);
                throw new AllEmployeeLookupException(errorMessage);
            }
        } else {
            String errorMessage = String.format("An error occurred. Http status: [%s]", statusCode);
            log.error(errorMessage);
            throw new AllEmployeeLookupException(errorMessage);
        }
    }

    public List<Employee> getEmployeesByName(String name) throws URISyntaxException, IOException {
        List<Employee> employees = getAllEmployees();
        return employees.stream()
                .filter(employee -> employee.getEmployeeName().contains(name))
                .toList();
    }

    public int getHighestSalaryOfEmployees() throws URISyntaxException, IOException {
        List<Employee> employees = getAllEmployees();

        Optional<Employee> employee =  employees.stream()
                .max(Comparator.comparing(Employee::getEmployeeAge));

        if (employee.isPresent()) {
            log.info("Returning employee with max salary");
            return employee.get().getEmployeeSalary();
        } else {
            String errorMsg = "No Employee with max Salary";
            log.error(errorMsg);
            throw new EmployeeNotFoundException(errorMsg);
        }
    }

    public List<String> getTop10HighestEarningEmployeeNames() throws URISyntaxException, IOException {
        List<Employee> employees = getAllEmployees();

        return employees.stream()
                .sorted(Comparator.comparing(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .toList().stream().map(Employee::getEmployeeName).toList();
    }

    public Employee getEmployeeById(String employeeId) throws URISyntaxException, IOException {

        if (StringUtils.isBlank(employeeId)) {
            throw new EmployeeIdLookupException("Employee Id cannot be blank");
        }

        Employee cachedEmployee = appLocalCache.getFromEmployeeCacheById(employeeId);

        if (cachedEmployee != null) {
            return cachedEmployee;
        }

        URIBuilder builder = new URIBuilder(employeeApiUrl.concat(EMPLOYEE_PATH).concat("/").concat(employeeId));
        HttpGet getRequest = new HttpGet(builder.build());

        log.info("Sending request to get employee by id {}", employeeId);
        HttpResponse response = closeableHttpClient.execute(getRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseString = EntityUtils.toString(response.getEntity());

        if (statusCode == HttpStatus.OK.value()) {
            try {
                JSONObject jsonResponse = new JSONObject(responseString);
                JSONObject jsonData = jsonResponse.getJSONObject("data");

                if(jsonData.isNull("id")) {
                    log.error("Employee with ID {} was not found", employeeId);
                    throw new EmployeeNotFoundException(String.format("Employee with ID %s was not found", employeeId));
                }

                return Employee.builder()
                        .id((jsonData.getInt("id")))
                        .employeeName(jsonData.getString("employee_name"))
                        .employeeSalary(jsonData.getInt("employee_salary"))
                        .employeeAge(jsonData.getInt("employee_age"))
                        .profileImage(jsonData.getString("profile_image"))
                        .build();
            } catch (JSONException e) {
                String errorMessage = e.getMessage();
                log.error("An error occurred {}", errorMessage);
                throw new EmployeeIdLookupException(errorMessage);
            }
        } else {
            String errorMessage = String.format("An error occurred. Http status: [%s]", statusCode);
            log.error(errorMessage);
            throw new EmployeeIdLookupException(errorMessage);
        }
    }

    public Employee createEmployee(Map<String, Object> employeeInput) throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(employeeApiUrl.concat(CREATE_EMPLOYEE_PATH));
        HttpPost postRequest = new HttpPost(builder.build());
        postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        StringEntity requestBody = new StringEntity(objectMapper.writeValueAsString(employeeInput));
        postRequest.setEntity(requestBody);

        log.info("Sending request to create employee");
        HttpResponse response = closeableHttpClient.execute(postRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseString = EntityUtils.toString(response.getEntity());

        if (statusCode == HttpStatus.OK.value()) {
            try {
                JSONObject jsonResponse = new JSONObject(responseString);
                JSONObject jsonData = jsonResponse.getJSONObject("data");

                Employee createdEmployee = Employee.builder()
                        .id((jsonData.getInt("id")))
                        .employeeName(jsonData.getString("name"))
                        .employeeSalary(jsonData.getInt("salary"))
                        .employeeAge(jsonData.getInt("age"))
                        .build();

                appLocalCache.addToEmployeeCacheById(String.valueOf(createdEmployee.getId()), createdEmployee);

                log.info("Employee created successfully. Returning response: [{}]", createdEmployee);
                return createdEmployee;
            } catch (JSONException e) {
                String errorMessage = e.getMessage();
                log.error("An error occurred {}", errorMessage);
                throw new EmployeeCreationException(errorMessage);
            }
        } else {
            String errorMessage = String.format("An error occurred. Http status: [%s]", statusCode);
            log.error(errorMessage);
            throw new EmployeeCreationException(errorMessage);
        }
    }

    public String deleteEmployeeById(String employeeId) throws URISyntaxException, IOException {

        if (StringUtils.isBlank(employeeId)) {
            throw new EmployeeDeleteException("Employee Id cannot be blank");
        }

        URIBuilder builder = new URIBuilder(employeeApiUrl.concat(DELETE_EMPLOYEE_PATH).concat("/").concat(employeeId));
        HttpDelete deleteRequest = new HttpDelete(builder.build());

        log.info("Sending request to delete employee id {}", employeeId);
        HttpResponse response = closeableHttpClient.execute(deleteRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseString = EntityUtils.toString(response.getEntity());

        if (statusCode == HttpStatus.OK.value()) {
            try {
                JSONObject jsonResponse = new JSONObject(responseString);

                appLocalCache.removeFromEmployeeCacheById(employeeId);

                log.info("Employee deleted successfully - returning response");
                return jsonResponse.getString("data");
            } catch (JSONException e) {
                String errorMessage = e.getMessage();
                log.error("An error occurred {}", errorMessage);
                throw new EmployeeCreationException(errorMessage);
            }
        } else {
            String errorMessage = String.format("An error occurred. Http status: [%s]", statusCode);
            log.error(errorMessage);
            throw new EmployeeCreationException(errorMessage);
        }
    }

}
