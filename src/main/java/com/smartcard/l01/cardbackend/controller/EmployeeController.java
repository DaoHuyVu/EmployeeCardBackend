package com.smartcard.l01.cardbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcard.l01.cardbackend.Gender;
import com.smartcard.l01.cardbackend.constants.Constants;
import com.smartcard.l01.cardbackend.request.EmployeeRequest;
import com.smartcard.l01.cardbackend.response.RestResponse;
import com.smartcard.l01.cardbackend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.spi.RegisterableService;
import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("")
    public ResponseEntity<?> addEmployee(
            @RequestBody EmployeeRequest request
            ){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        employeeService.addEmployee(
                request.getId(),
                request.getName(),
                request.getGender().length() == 3 ? Gender.MALE.toString() : Gender.FEMALE.toString(),
                LocalDate.parse(request.getDateOfBirth(),dateTimeFormatter),
                0L,
                request.getPinCode(),
                request.getPublicKey()
        );
        return ResponseEntity.ok().body(new RestResponse(
                HttpStatus.CREATED.toString(),
                Constants.CREATED_SUCCESSFULLY
        ));
    }
    @GetMapping("/latestId")
    public ResponseEntity<?> getLatestEmployeeId(){
        return ResponseEntity.ok().body(employeeService.getLatestEmployeeId());
    }
    @PutMapping("")
    public ResponseEntity<?> updateInfo(
            @RequestParam("fields") String f,
            @RequestParam("id") Long employeeId
    ) throws JsonProcessingException {
        Map<String,String> fields = mapper.readValue(f,new TypeReference<>(){});
        employeeService.updateInfo(fields,employeeId);
        return ResponseEntity.ok().build();
    }
    @GetMapping(value = "publicKey")
    public ResponseEntity<?> getPublicKey(
            @RequestParam("employeeId") String employeeId
    ){
        return ResponseEntity.ok().body(employeeService.getPublicKey(employeeId));
    }
    @PostMapping("/checkIn")
    public ResponseEntity<?> checkIn(@RequestParam Long employeeId) {
        employeeService.checkIn(employeeId);
        return ResponseEntity.ok().body(new RestResponse(
                HttpStatus.CREATED.toString(),
                Constants.CHECK_IN_SUCCESSFULLY
        ));
    }
    @PostMapping("/checkOut")
    public ResponseEntity<?> checkOut(@RequestParam Long employeeId) {
        employeeService.checkOut(employeeId);
        return ResponseEntity.ok().body(new RestResponse(
                HttpStatus.CREATED.toString(),
                Constants.CHECK_OUT_SUCCESSFULLY
        ));
    }
    @GetMapping("/checkInDates")
    public ResponseEntity<?> getCheckInDates(@RequestParam Long employeeId) {
        return ResponseEntity.ok().body(employeeService.getCheckInDates(employeeId));
    }
    @GetMapping("/hasActiveSession")
    public ResponseEntity<?> hasActiveSession(@RequestParam Long employeeId) {
        boolean hasActiveRecord = employeeService.hasActiveSession(employeeId);
        return ResponseEntity.ok().body(new RestResponse(
                HttpStatus.OK.toString(),
                hasActiveRecord ? "Active Session record exists." : "No active time keeping record."
        ));
    }
    @PutMapping("/withdrawal")
    public ResponseEntity<?> withdrawal(
            @RequestParam("balance") Long balance,
            @RequestParam("id") Long id
    ){
        employeeService.withdrawal(balance,id);
        return ResponseEntity.ok().body("Update successfully");
    }
}
