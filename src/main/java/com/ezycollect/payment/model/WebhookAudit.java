package com.ezycollect.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_webhooks")
public class WebhookAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private String error;

}
