package com.smartcard.l01.cardbackend.repository;

import com.smartcard.l01.cardbackend.dao.EmployeeCardMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeCardMetadataRepository extends JpaRepository<EmployeeCardMetadata,Long> {

}
