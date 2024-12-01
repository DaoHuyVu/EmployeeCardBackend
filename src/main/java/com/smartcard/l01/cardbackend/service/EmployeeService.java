package com.smartcard.l01.cardbackend.service;

import com.smartcard.l01.cardbackend.Gender;
import com.smartcard.l01.cardbackend.dao.Employee;
import com.smartcard.l01.cardbackend.dao.EmployeeCardMetadata;
import com.smartcard.l01.cardbackend.repository.EmployeeCardMetadataRepository;
import com.smartcard.l01.cardbackend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeCardMetadataRepository employeeCardMetadataRepository;
    @Transactional
    public void addEmployee(
            String employeeId,
            String name,
            String g,
            LocalDate dateOfBirth,
            Double balance,
            String pinCode,
            String publicKey
    ){
        Gender gender = g.equalsIgnoreCase("MALE") ? Gender.MALE : Gender.FEMALE;

        Employee employee = new Employee(employeeId,name,gender,dateOfBirth,balance);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        EmployeeCardMetadata employeeCardMetadata = new EmployeeCardMetadata(
                employee.getId(),
                employee,
                pinCode,
                publicKey,
                false,
                0L,
                now,
                now
        );
        employeeRepository.save(employee);
        employeeCardMetadataRepository.save(employeeCardMetadata);
    }
}
