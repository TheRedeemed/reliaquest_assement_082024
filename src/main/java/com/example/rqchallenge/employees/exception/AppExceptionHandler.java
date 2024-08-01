package com.example.rqchallenge.employees.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {AllEmployeeLookupException.class})
    protected ResponseEntity<Object> handleAllEmployeeLookupException(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during all employee lookup.");
    }

    @ExceptionHandler(value = {EmployeeIdLookupException.class})
    protected ResponseEntity<Object> handleEmployeeIdLookupException(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An unexpected error occurred during employee lookup by ID.");
    }

    @ExceptionHandler(value = {EmployeeNotFoundException.class})
    protected ResponseEntity<Object> handleEmployeeNotFoundException(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
    }

    @ExceptionHandler(value = {EmployeeCreationException.class})
    protected ResponseEntity<Object> handleEmployeeCreationException(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating an Employee.");
    }

    @ExceptionHandler(value = {EmployeeDeleteException.class})
    protected ResponseEntity<Object> handleEmployeeDeleteException(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting an Employee.");
    }
}
