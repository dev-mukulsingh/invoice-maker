package com.invoicemaker.dto;

import com.invoicemaker.entity.InvoiceStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequestDTO {

    @NotNull(message = "Status cannot be null")
    private InvoiceStatus status;

    public StatusUpdateRequestDTO() {}

    public StatusUpdateRequestDTO(InvoiceStatus status) {
        this.status = status;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
}
