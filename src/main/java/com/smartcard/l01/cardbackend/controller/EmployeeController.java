package com.smartcard.l01.cardbackend.controller;

import com.smartcard.l01.cardbackend.constants.Constants;
import com.smartcard.l01.cardbackend.response.RestResponse;
import com.smartcard.l01.cardbackend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("")
    public ResponseEntity<?> addEmployee(
            @RequestParam("id") String id,
            @RequestParam("name") String name,
            @RequestParam("gender") String gender,
            @RequestParam("dateOfBirth") LocalDate dateOfBirth,
            @RequestParam("balance") Double balance,
            @RequestParam("pinCode") String pinCode,
            @RequestParam("publicKey") String publicKey
            ){
        employeeService.addEmployee(id,name,gender,dateOfBirth,balance,pinCode,publicKey);
        return ResponseEntity.ok().body(new RestResponse(
                HttpStatus.CREATED.toString(),
                Constants.CREATED_SUCCESSFULLY
        ));
    }
}
