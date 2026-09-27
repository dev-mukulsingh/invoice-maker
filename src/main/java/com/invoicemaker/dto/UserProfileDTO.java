package com.invoicemaker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserProfileDTO {
    private Long id;

    @NotBlank(message = "Business name cannot be blank")
    private String businessName;

    @NotBlank(message = "Business email cannot be blank")
    @Email(message = "Valid email is required")
    private String email;

    private String gstNumber;
    private String address;
    private String phone;

    public UserProfileDTO() {}

    public UserProfileDTO(Long id, String businessName, String email, String gstNumber, String address, String phone) {
        this.id = id;
        this.businessName = businessName;
        this.email = email;
        this.gstNumber = gstNumber;
        this.address = address;
        this.phone = phone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String businessName;
        private String email;
        private String gstNumber;
        private String address;
        private String phone;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder businessName(String businessName) { this.businessName = businessName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder gstNumber(String gstNumber) { this.gstNumber = gstNumber; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }

        public UserProfileDTO build() {
            return new UserProfileDTO(id, businessName, email, gstNumber, address, phone);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
