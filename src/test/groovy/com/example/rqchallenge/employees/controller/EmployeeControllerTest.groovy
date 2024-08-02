package com.example.rqchallenge.employees.controller

import com.example.rqchallenge.employees.models.Employee
import com.example.rqchallenge.employees.service.EmployeeService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import spock.lang.Specification

import static com.example.rqchallenge.employees.mocks.EmployeeMocks.getEmployeeMock
import static com.example.rqchallenge.employees.mocks.EmployeeMocks.getEmployeeRequest
import static com.example.rqchallenge.employees.mocks.EmployeeMocks.getEmployeesListMock
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.when

@ActiveProfiles("test")
@SpringBootTest(classes = EmployeeController.class)
@EnableWebMvc
class EmployeeControllerTest extends Specification {

    @Autowired
    private WebApplicationContext webApplicationContext
    private MockMvc mockMvc

    @MockBean
    private EmployeeService employeeService

    List<Employee> employeeList
    Employee employee
    Map<String, Object> createEmployeeRequest

    def setup() {
        employeeList = getEmployeesListMock()
        employee = getEmployeeMock()
        createEmployeeRequest = getEmployeeRequest()
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    def 'Get List of all employees'() {
        given: 'A request to get all employees'

        when: 'The get employees endpoint is called'
        when(employeeService.getAllEmployees()).thenReturn(employeeList)

        then: 'A response with Http OK status'
        mockMvc.perform(MockMvcRequestBuilders.get('/employees'))
                .andExpect (MockMvcResultMatchers.status().isOk())
    }

    def 'Get List of all employees by name'() {
        given: 'A request to get all employees by name'

        when: 'The get employees by name endpoint is called'
        when(employeeService.getEmployeesByName(any())).thenReturn(employeeList)

        then: 'A response with Http OK status'
        mockMvc.perform(MockMvcRequestBuilders.get('/employees/joe'))
                .andExpect (MockMvcResultMatchers.status().isOk())
    }

    def 'Get employee by ID'() {
        given: 'A request to get an employees by ID'

        when: 'The get employees by ID endpoint is called'
        when(employeeService.getEmployeeById(any())).thenReturn(employee)

        then: 'A response with Http OK status'
        mockMvc.perform(MockMvcRequestBuilders.get('/employees/1'))
                .andExpect (MockMvcResultMatchers.status().isOk())
    }

    def 'Get List of employees with highest salary'() {
        given: 'A request to get employee with highest salary'

        when: 'The get highest salary employees endpoint is called'
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(35000)

        then: 'A response with Http OK status'
        mockMvc.perform(MockMvcRequestBuilders.get('/employees/highest-salary'))
                .andExpect (MockMvcResultMatchers.status().isOk())
    }

    def 'Get List of top ten employees with highest salary'() {
        given: 'A request to get the top ten employees with highest salary'

        when: 'The get to top ten highest salary employees endpoint is called'
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(List.of('Joe Tester'))

        then: 'A response with Http OK status'
        mockMvc.perform(MockMvcRequestBuilders.get('/employees/top-ten-highest-earning-employee-names'))
                .andExpect (MockMvcResultMatchers.status().isOk())
    }

    def 'Create an employee'() {
        given: 'A request to create an employee'

        when: 'The create employee endpoint is called'
        when(employeeService.createEmployee(createEmployeeRequest)).thenReturn(employee)

        then: 'A response with Http OK status'
        mockMvc.perform(MockMvcRequestBuilders.post('/employees')
                .content(toJsonString(createEmployeeRequest)))
                .andExpect (MockMvcResultMatchers.status().isOk())
    }

    def 'Delete an employee'() {
        given: 'A request to delete an employee'

        when: 'The delete employee endpoint is called'
        when(employeeService.deleteEmployeeById('1')).thenReturn('1')

        then: 'A response with Http OK status'
        mockMvc.perform(MockMvcRequestBuilders.delete('/employees'))
                .andExpect (MockMvcResultMatchers.status().isOk())
    }

    private static String toJsonString(Object obj) {
        try{
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj)
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }
}
