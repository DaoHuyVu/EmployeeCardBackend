package com.smartcard.l01.cardbackend.repository;

import com.smartcard.l01.cardbackend.dao.EmployeeTimeKeeping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmployeeTimeKeepingRepository extends JpaRepository<EmployeeTimeKeeping, Long> {
    @Modifying
    @Query("UPDATE EmployeeTimeKeeping etk SET etk.timeOut = :timeOut WHERE etk.employee.id = :employeeId AND etk.timeOut IS NULL")
    void updateTimeOut(@Param("employeeId") Long employeeId, @Param("timeOut") LocalDateTime timeOut);
    @Query("SELECT etk FROM EmployeeTimeKeeping etk WHERE etk.employee.id = :employeeId AND etk.timeOut IS NULL")
    EmployeeTimeKeeping findActiveTimeKeepingByEmployeeId(@Param("employeeId") Long employeeId);
    @Query("SELECT etk FROM EmployeeTimeKeeping etk WHERE etk.employee.id = :employeeId")
    List<EmployeeTimeKeeping> findByEmployeeId(@Param("employeeId") Long employeeId);
    @Query("SELECT COUNT(etk) > 0 FROM EmployeeTimeKeeping etk WHERE etk.employee.id = :employeeId AND etk.timeOut IS NULL")
    boolean existsActiveSessionByEmployeeId(@Param("employeeId") Long employeeId);
}
