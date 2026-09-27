package com.invoicemaker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class ClientDTO {
    private Long id;

    @NotBlank(message = "Client name cannot be blank")
    private String clientName;

    @NotBlank(message = "Client email cannot be blank")
    @Email(message = "Valid client email is required")
    private String clientEmail;

    private String clientPhone;
    private String billingAddress;
    private String gstNumber;
    private LocalDateTime createdAt;

    public ClientDTO() {}

    public ClientDTO(Long id, String clientName, String clientEmail, String clientPhone, String billingAddress, String gstNumber, LocalDateTime createdAt) {
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

        public ClientDTO build() {
            return new ClientDTO(id, clientName, clientEmail, clientPhone, billingAddress, gstNumber, createdAt);
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
}
