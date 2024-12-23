package com.smartcard.l01.cardbackend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeRequest {
    private String id;
    private String name;
    private  String gender;
    private String dateOfBirth;
    private String pinCode;
    private  String publicKey;
}
