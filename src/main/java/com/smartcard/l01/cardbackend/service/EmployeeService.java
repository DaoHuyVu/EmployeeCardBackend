package com.smartcard.l01.cardbackend.service;

import com.smartcard.l01.cardbackend.Gender;
import com.smartcard.l01.cardbackend.dao.Employee;
import com.smartcard.l01.cardbackend.dao.EmployeeCardMetadata;
import com.smartcard.l01.cardbackend.repository.EmployeeCardMetadataRepository;
import com.smartcard.l01.cardbackend.repository.EmployeeRepository;
import org.apache.el.util.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
            Long balance,
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
                now
        );
        employeeRepository.save(employee);
        employeeCardMetadataRepository.save(employeeCardMetadata);
    }
    public Long getLatestEmployeeId(){
        return employeeRepository.findLatestEmployeeId();
    }
    @Transactional
    public void updateInfo(Map<String,String> fields,String employeeId){
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        fields.forEach((key,value) -> {
            Field field = ReflectionUtils.findField(Employee.class,key);
            if(field != null){
                field.setAccessible(true);
                if(field.getType().getCanonicalName().equals(String.class.getCanonicalName())){
                    ReflectionUtils.setField(field,employee,value);
                }
                else if(field.getType().getCanonicalName().equals(Long.class.getCanonicalName())){
                    Long newValue = Long.parseLong(value);
                    ReflectionUtils.setField(field,employee,newValue);
                }
                else if(field.getType().getCanonicalName().equals(LocalDate.class.getCanonicalName())){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate newValue = LocalDate.parse(value,formatter);
                    ReflectionUtils.setField(field,employee,newValue);
                }
                else if (field.getType().getCanonicalName().equals(Gender.class.getCanonicalName())){
                    if(value.length() == 3){
                        ReflectionUtils.setField(field,employee,Gender.MALE);
                    }
                    else
                        ReflectionUtils.setField(field,employee,Gender.FEMALE);
                    }
                }
            else {
                field = ReflectionUtils.findField(EmployeeCardMetadata.class,key);
                if(field != null){
                    if(field.getType().getCanonicalName().equals(Boolean.class.getCanonicalName())){
                        employeeCardMetadataRepository.upLockState(Boolean.getBoolean(value),employee.getId());
                    }
                    if(field.getType().getCanonicalName().equals(String.class.getCanonicalName())){
                        switch (key){
                            case "pinCode":{
                                employeeCardMetadataRepository.changePin(value,employee.getId());
                                break;
                            }
                            case "publicKey" : {
                                employeeCardMetadataRepository.changePublicKey(value,employee.getId());
                                break;
                            }
                            default:{
                                throw new RuntimeException("Don't find any string fields that match");
                            }
                        }
                    }
                }
                else
                    throw new RuntimeException("Field not found");
            }
        });
        employeeRepository.save(employee);
    }
}
