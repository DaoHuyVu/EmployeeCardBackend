package com.smartcard.l01.cardbackend.repository;

import com.smartcard.l01.cardbackend.dao.EmployeeCardMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeCardMetadataRepository extends JpaRepository<EmployeeCardMetadata,Long> {
    @Query("UPDATE EmployeeCardMetadata ecmd set ecmd.pinCode = :pinCode where ecmd.employee.employeeId = :id")
    @Modifying
    void changePin(@Param("pinCode") String pinCode,@Param("id") Long id);
    @Query("UPDATE EmployeeCardMetadata ecmd set ecmd.isLock = :isLock where ecmd.employee.employeeId = :id")
    @Modifying
    void upLockState(@Param("isLock") Boolean isLock,@Param("id") Long id);
    @Query("UPDATE EmployeeCardMetadata ecmd set ecmd.publicKey = :publicKey where ecmd.employee.employeeId = :id")
    @Modifying
    void changePublicKey(@Param("publicKey") String publicKey,@Param("id") Long id);

    @Query("""
            SELECT ecmd.publicKey from EmployeeCardMetadata ecmd where ecmd.employee.employeeId = :id
            """)
    String findPublicKeyByEId(@Param("id") String id);
}
