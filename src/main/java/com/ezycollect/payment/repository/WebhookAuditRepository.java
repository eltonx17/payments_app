package com.ezycollect.payment.repository;

import com.ezycollect.payment.model.WebhookAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookAuditRepository extends JpaRepository<WebhookAudit, Long> {
}

