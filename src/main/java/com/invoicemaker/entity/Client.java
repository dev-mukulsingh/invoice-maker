package com.invoicemaker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Client name is required")
    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Email(message = "Valid client email is required")
    @NotBlank(message = "Client email is required")
    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Column(name = "client_phone", length = 30)
    private String clientPhone;

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Column(name = "gst_number", length = 30)
    private String gstNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Client() {}

    public Client(Long id, String clientName, String clientEmail, String clientPhone, String billingAddress, String gstNumber, LocalDateTime createdAt) {
        this.id = id;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.billingAddress = billingAddress;
        this.gstNumber = gstNumber;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String clientName;
        private String clientEmail;
        private String clientPhone;
        private String billingAddress;
        private String gstNumber;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder clientName(String clientName) { this.clientName = clientName; return this; }
        public Builder clientEmail(String clientEmail) { this.clientEmail = clientEmail; return this; }
        public Builder clientPhone(String clientPhone) { this.clientPhone = clientPhone; return this; }
        public Builder billingAddress(String billingAddress) { this.billingAddress = billingAddress; return this; }
        public Builder gstNumber(String gstNumber) { this.gstNumber = gstNumber; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Client build() {
            return new Client(id, clientName, clientEmail, clientPhone, billingAddress, gstNumber, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
