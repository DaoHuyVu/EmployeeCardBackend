package com.smartcard.l01.cardbackend.repository;

import com.smartcard.l01.cardbackend.dao.Employee;
import com.smartcard.l01.cardbackend.dao.EmployeeCardMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    @Query(value = "SELECT IFNULL(MAX(e.id),0) from EMPLOYEE e",nativeQuery = true)
    Long findLatestEmployeeId();
    Employee findByEmployeeId(String employeeId);
}
