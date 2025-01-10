package com.smartcard.l01.cardbackend.service;

import com.smartcard.l01.cardbackend.Gender;
import com.smartcard.l01.cardbackend.dao.Employee;
import com.smartcard.l01.cardbackend.dao.EmployeeCardMetadata;
import com.smartcard.l01.cardbackend.dao.EmployeeTimeKeeping;
import com.smartcard.l01.cardbackend.repository.EmployeeCardMetadataRepository;
import com.smartcard.l01.cardbackend.repository.EmployeeRepository;
import com.smartcard.l01.cardbackend.repository.EmployeeTimeKeepingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeCardMetadataRepository employeeCardMetadataRepository;
    @Autowired
    private EmployeeTimeKeepingRepository employeeTimeKeepingRepository;
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
    public void updateInfo(Map<String,String> fields,Long employeeId){
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
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
                employeeRepository.save(employee);
            }
            else {
                field = ReflectionUtils.findField(EmployeeCardMetadata.class,key);
                if(field != null){
                    switch (key){
                        case "pinCode":{
                            System.out.println("Change pinCode to " + value);
                            employeeCardMetadataRepository.changePin(value,employee.getId());
                            break;
                        }
                        case "publicKey" : {
                            employeeCardMetadataRepository.changePublicKey(value,employee.getId());
                            break;
                        }
                        case "isLock" : {
                            System.out.println("Change isLock to " + value);
                            employeeCardMetadataRepository.upLockState(Boolean.parseBoolean(value),employee.getId());
                            break;
                        }
                        default:{
                            throw new RuntimeException("Don't find any string fields that match");
                        }
                    }
                }
                else
                    throw new RuntimeException("Field not found");
            }
        });
    }
    public String getPublicKey(String employeeId){
        return employeeCardMetadataRepository.findPublicKeyByEmployeeId(employeeId);
    }
    @Transactional
    public void checkIn(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        EmployeeTimeKeeping timeKeeping = new EmployeeTimeKeeping(employee, LocalDateTime.now(), null);
        employeeTimeKeepingRepository.save(timeKeeping);
    }

    public boolean checkDate(LocalDateTime timeIn, LocalDateTime timeOut) {
        if(timeIn == null || timeOut == null) {
            return false;
        }
        return timeIn.toLocalDate().equals(timeOut.toLocalDate());
    }
    @Transactional
    public void checkOut(Long employeeId) {
        EmployeeTimeKeeping activeTimeKeeping = employeeTimeKeepingRepository.findActiveTimeKeepingByEmployeeId(employeeId);
        if (activeTimeKeeping == null) {
            throw new RuntimeException("No active check-in record found for this employee");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!checkDate(activeTimeKeeping.getTimeIn(), now)) {
            throw new RuntimeException("Check-out must be on the same day as check-in");
        }

        activeTimeKeeping.setTimeOut(now);
        employeeTimeKeepingRepository.save(activeTimeKeeping);
    }

    public List<LocalDate> getCheckInDates(Long employeeId) {
        List<EmployeeTimeKeeping> timeKeepings = employeeTimeKeepingRepository.findByEmployeeId(employeeId);
        if (timeKeepings.isEmpty()) {
            throw new RuntimeException("No check-in records found for this employee");
        }

        return timeKeepings.stream()
                .map(EmployeeTimeKeeping::getTimeIn)
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted()
                .toList();
    }
    public boolean hasActiveSession(Long employeeId) {
        return employeeTimeKeepingRepository.existsActiveSessionByEmployeeId(employeeId);
    }
    @Transactional
    public void withdrawal(Long balance,Long employeeId){
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        employee.setBalance(employee.getBalance() - balance);
        employeeRepository.save(employee);
    }
}
