package com.ezycollect.payment.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String cardNumber;
}
