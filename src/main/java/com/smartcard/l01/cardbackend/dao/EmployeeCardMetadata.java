package com.smartcard.l01.cardbackend.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCardMetadata {
    @Id
    private Long id;
    @JoinColumn(name = "employee_id")
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Employee employee;
    @Column(name = "pin_code",nullable = false,length = 10)
    private String pinCode;
    @Column(name = "public_key")
    private String publicKey;
    @Column(name = "is_lock",nullable = false)
    private Boolean isLock;
    @Column(name = "created_at",nullable = false)
    private ZonedDateTime createdAt;
}
