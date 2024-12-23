package com.smartcard.l01.cardbackend.dao;

import com.smartcard.l01.cardbackend.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employee_id",unique = true,length = 8)
    private String employeeId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "date_of_birth",nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private Long balance;

    public Employee(String employeeId,String name, Gender gender, LocalDate dateOfBirth, Long balance) {
        this.employeeId = employeeId;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.balance = balance;
    }
}
