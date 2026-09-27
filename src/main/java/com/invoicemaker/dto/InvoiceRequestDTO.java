package com.invoicemaker.dto;

import com.invoicemaker.entity.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRequestDTO {

    private String invoiceNumber;

    @NotNull(message = "Client must be selected")
    private Long clientId;

    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Selected template is required")
    @Min(value = 1, message = "Template must be between 1 and 10")
    @Max(value = 10, message = "Template must be between 1 and 10")
    private Integer selectedTemplate = 1;

    private InvoiceStatus status = InvoiceStatus.UNPAID;
    private String notes;

    @NotEmpty(message = "Invoice must contain at least one item")
    @Valid
    private List<InvoiceItemDTO> items = new ArrayList<>();

    public InvoiceRequestDTO() {}

    public InvoiceRequestDTO(String invoiceNumber, Long clientId, LocalDate invoiceDate, LocalDate dueDate,
                             Integer selectedTemplate, InvoiceStatus status, String notes, List<InvoiceItemDTO> items) {
        this.invoiceNumber = invoiceNumber;
        this.clientId = clientId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.selectedTemplate = selectedTemplate != null ? selectedTemplate : 1;
        this.status = status != null ? status : InvoiceStatus.UNPAID;
        this.notes = notes;
        this.items = items != null ? items : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String invoiceNumber;
        private Long clientId;
        private LocalDate invoiceDate;
        private LocalDate dueDate;
        private Integer selectedTemplate = 1;
        private InvoiceStatus status = InvoiceStatus.UNPAID;
        private String notes;
        private List<InvoiceItemDTO> items = new ArrayList<>();

        public Builder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public Builder clientId(Long clientId) { this.clientId = clientId; return this; }
        public Builder invoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder selectedTemplate(Integer selectedTemplate) { this.selectedTemplate = selectedTemplate; return this; }
        public Builder status(InvoiceStatus status) { this.status = status; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder items(List<InvoiceItemDTO> items) { this.items = items; return this; }

        public InvoiceRequestDTO build() {
            return new InvoiceRequestDTO(invoiceNumber, clientId, invoiceDate, dueDate, selectedTemplate, status, notes, items);
        }
    }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Integer getSelectedTemplate() { return selectedTemplate; }
    public void setSelectedTemplate(Integer selectedTemplate) { this.selectedTemplate = selectedTemplate; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }
}
