package com.example.rqchallenge.employees.service

import com.example.rqchallenge.employees.exception.AllEmployeeLookupException
import com.example.rqchallenge.employees.exception.EmployeeCreationException
import com.example.rqchallenge.employees.exception.EmployeeDeleteException
import com.example.rqchallenge.employees.exception.EmployeeIdLookupException
import com.example.rqchallenge.employees.exception.EmployeeNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.StatusLine
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

import static com.example.rqchallenge.employees.mocks.EmployeeMocks.*

class EmployeeServiceTest extends Specification {

    private CloseableHttpClient closeableHttpClient
    private ObjectMapper objectMapper

    private CloseableHttpResponse closeableHttpResponse
    private StatusLine statusLine
    private String noEmployeesFoundResponse
    private String employeesFoundResponse
    private String noEmployeeByIdFoundResponse
    private String employeeByIdFoundResponse
    private Map<String, Object> createEmployeeRequest
    private String createEmployeeResponse
    private String deleteEmployeeResponse

    private EmployeeService employeeService

    def setup() {
        closeableHttpClient = Mock()
        objectMapper = Mock()
        closeableHttpResponse = Mock()
        statusLine = Mock()

        noEmployeesFoundResponse = getAllEmployeeEmptyResponse()
        employeesFoundResponse = getAllEmployeeResponse()
        noEmployeeByIdFoundResponse = getEmployeeByIdNotFound()
        employeeByIdFoundResponse = getEmployeeByIdFound()
        createEmployeeRequest = getEmployeeRequest()
        createEmployeeResponse = getCreateEmployeeResponse()
        deleteEmployeeResponse = getDeleteEmployeeResponse()

        employeeService = new EmployeeService(closeableHttpClient, objectMapper)
        ReflectionTestUtils.setField(employeeService, "employeeApiUrl", "http://some-test-url")
    }

    def 'Request to get all employees - status code is not 200'() {
        given: 'A request to get all employees'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 500

        when: 'The getAllEmployees method is called and an error occurs'
        employeeService.getAllEmployees()

        then: 'Except an AllEmployeeLookupException to be thrown'
        def error = thrown(AllEmployeeLookupException)
        error.message == 'An error occurred. Http status: [500]'
    }

    def 'Request to get all employees - JSON Error'() {
        given: 'A request to get all employees'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream()
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The getAllEmployees method is called and A JSON Error occurs'
        employeeService.getAllEmployees()

        then: 'Except an AllEmployeeLookupException to be thrown'
        thrown(AllEmployeeLookupException)
    }

    def 'Request to get all employees - No employees found'() {
        given: 'A request to get all employees'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream(noEmployeesFoundResponse.getBytes())
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The getAllEmployees method is called and the data array is empty'
        def response = employeeService.getAllEmployees()

        then: 'An empty list to be returned'
        response.isEmpty()
    }

    def 'Request to get all employees'() {
        given: 'A request to get all employees'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream(employeesFoundResponse.getBytes())
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The getAllEmployees method is called and the data array is not empty'
        def response = employeeService.getAllEmployees()

        then: 'Except the list of employee to be not be empty'
        !response.isEmpty()
    }

    def 'Request to get employee by ID - status code is not 200 '() {
        given: 'A request to get an employee by ID'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 500

        when: 'The getEmployeeById method is called and an error occurs'
        employeeService.getEmployeeById('1')

        then: 'Except an EmployeeIdLookupException to be thrown'
        def error = thrown(EmployeeIdLookupException)
        error.message == 'An error occurred. Http status: [500]'
    }

    def 'Request to get employee by ID - JSON Error'() {
        given: 'A request to get an employee by ID'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream()
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The getEmployeeById method is called and A JSON Error occurs'
        employeeService.getEmployeeById('1')

        then: 'Except an EmployeeIdLookupException to be thrown'
        thrown(EmployeeIdLookupException)
    }

    def 'Request to get employee by ID - No employee found by ID'() {
        given: 'A request to get an employee by ID'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream(noEmployeeByIdFoundResponse.getBytes())
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The getEmployeeById method is called and the employee is not found'
        employeeService.getEmployeeById('1000')

        then: 'Except an EmployeeNotFoundException to be thrown'
        thrown(EmployeeNotFoundException)
    }

    def 'Request to get employee by ID - employee found by ID'() {
        given: 'A request to get an employee by ID'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream(employeeByIdFoundResponse.getBytes())
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The getEmployeeById method is called and the employee is found'
        def response = employeeService.getEmployeeById('1000')

        then: 'Except an EmployeeNotFoundException to be thrown'
        response.employeeName == 'Tiger Nixon'
        response.employeeAge == 61
    }

    def 'Request to create an employee - status code is not 200'() {
        given: 'A request to create an employee'
        closeableHttpClient.execute(_ as HttpPost) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 500

        when: 'The createEmployee method is called and the response status code is not 200'
        employeeService.createEmployee(createEmployeeRequest)

        then: 'Expect an EmployeeCreationException to be thrown'
        def error = thrown(EmployeeCreationException)
        error.message == 'An error occurred. Http status: [500]'
    }

    def 'Request to create an employee - JSON Error'() {
        given: 'A request to create an employee'
        closeableHttpClient.execute(_ as HttpPost) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream()
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The createEmployee method is called and a JSON Error occurs'
        employeeService.createEmployee(createEmployeeRequest)

        then: 'Expect an EmployeeCreationException to be thrown'
        thrown(EmployeeCreationException)
    }

    def 'Request to create an employee'() {
        given: 'A request to create an employee'
        closeableHttpClient.execute(_ as HttpPost) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream(createEmployeeResponse.getBytes())
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The createEmployee method is called and the user is created successfully'
        def response = employeeService.createEmployee(createEmployeeRequest)

        then: 'Expect a response to be returned'
        response.employeeName == 'Joe Tester'
        response.employeeSalary == 35000
        response.employeeAge == 35
    }

    def 'Request to delete an employee - Employee Id is blank'() {
        given: 'A request to delete an employee'

        when: 'The deleteEmployeeById method is called and the employee ID is blank'
        employeeService.deleteEmployeeById("")

        then: 'Expect an EmployeeDeleteException to be thrown'
        def error = thrown(EmployeeDeleteException)
        error.message == 'Employee Id cannot be blank'
    }

    def 'Request to delete an employee - status code is not 200'() {
        given: 'A request to delete an employee'
        closeableHttpClient.execute(_ as HttpDelete) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 500

        when: 'The deleteEmployeeById method is called and the employee ID is blank'
        employeeService.deleteEmployeeById("")

        then: 'Expect an EmployeeDeleteException to be thrown'
        def error = thrown(EmployeeDeleteException)
        error.message == 'An error occurred. Http status: [500]'
    }

    def 'Request to delete an employee - JSON Error'() {
        given: 'A request to delete an employee'
        closeableHttpClient.execute(_ as HttpDelete) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream()
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The deleteEmployeeById method is called and the employee ID is blank'
        employeeService.deleteEmployeeById("25")

        then: 'Expect an EmployeeDeleteException to be thrown'
        thrown(EmployeeDeleteException)
    }

    def 'Request to delete an employee - status code is not 200'() {
        given: 'A request to delete an employee'
        closeableHttpClient.execute(_ as HttpDelete) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream(deleteEmployeeResponse.getBytes())
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The deleteEmployeeById method is called and the employee ID is blank'
        def response = employeeService.deleteEmployeeById("25")

        then: 'Expect an EmployeeDeleteException to be thrown'
        response == '25'
    }

    def 'Request to get with employee with highest salary - EmployeeNotFoundException'() {
        given: 'A request to get employee with highest salary'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream(noEmployeesFoundResponse.getBytes())
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The getHighestSalaryOfEmployees method is called and the list of employee is empty'
        employeeService.getHighestSalaryOfEmployees()

        then: 'Except an EmployeeNotFoundException to be thrown'
        def error = thrown(EmployeeNotFoundException)
        error.message == 'No Employee with max Salary'
    }

    def 'Request to get with employee with highest salary'() {
        given: 'A request to get employee with highest salary'
        closeableHttpClient.execute(_ as HttpGet) >> closeableHttpResponse
        closeableHttpResponse.getStatusLine() >> statusLine
        statusLine.getStatusCode() >> 200
        BasicHttpEntity httpEntity = new BasicHttpEntity()
        InputStream inputStream = new ByteArrayInputStream(employeesFoundResponse.getBytes())
        httpEntity.setContent(inputStream)
        closeableHttpResponse.getEntity() >> httpEntity

        when: 'The getHighestSalaryOfEmployees method is called and the list of employee is not empty'
        def response = employeeService.getHighestSalaryOfEmployees()

        then: 'Except the highest salary to be returned'
        response == 320800
    }
}
