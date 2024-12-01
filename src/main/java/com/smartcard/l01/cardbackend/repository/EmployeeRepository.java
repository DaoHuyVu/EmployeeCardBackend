package com.smartcard.l01.cardbackend.repository;

import com.smartcard.l01.cardbackend.dao.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
