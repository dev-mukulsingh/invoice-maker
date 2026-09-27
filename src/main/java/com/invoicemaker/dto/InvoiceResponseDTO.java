package com.invoicemaker.dto;

import com.invoicemaker.entity.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal subtotal;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal grandTotal;
    private InvoiceStatus status;
    private Integer selectedTemplate;
    private String templateName;
    private String notes;
    private LocalDateTime createdAt;
    private ClientDTO client;
    private UserProfileDTO user;
    private List<InvoiceItemDTO> items = new ArrayList<>();

    public InvoiceResponseDTO() {}

    public InvoiceResponseDTO(Long id, String invoiceNumber, LocalDate invoiceDate, LocalDate dueDate,
                              BigDecimal subtotal, BigDecimal cgst, BigDecimal sgst, BigDecimal grandTotal,
                              InvoiceStatus status, Integer selectedTemplate, String templateName,
                              String notes, LocalDateTime createdAt, ClientDTO client,
                              UserProfileDTO user, List<InvoiceItemDTO> items) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.subtotal = subtotal;
        this.cgst = cgst;
        this.sgst = sgst;
        this.grandTotal = grandTotal;
        this.status = status;
        this.selectedTemplate = selectedTemplate;
        this.templateName = templateName;
        this.notes = notes;
        this.createdAt = createdAt;
        this.client = client;
        this.user = user;
        this.items = items != null ? items : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String invoiceNumber;
        private LocalDate invoiceDate;
        private LocalDate dueDate;
        private BigDecimal subtotal;
        private BigDecimal cgst;
        private BigDecimal sgst;
        private BigDecimal grandTotal;
        private InvoiceStatus status;
        private Integer selectedTemplate;
        private String templateName;
        private String notes;
        private LocalDateTime createdAt;
        private ClientDTO client;
        private UserProfileDTO user;
        private List<InvoiceItemDTO> items = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public Builder invoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public Builder cgst(BigDecimal cgst) { this.cgst = cgst; return this; }
        public Builder sgst(BigDecimal sgst) { this.sgst = sgst; return this; }
        public Builder grandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; return this; }
        public Builder status(InvoiceStatus status) { this.status = status; return this; }
        public Builder selectedTemplate(Integer selectedTemplate) { this.selectedTemplate = selectedTemplate; return this; }
        public Builder templateName(String templateName) { this.templateName = templateName; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder client(ClientDTO client) { this.client = client; return this; }
        public Builder user(UserProfileDTO user) { this.user = user; return this; }
        public Builder items(List<InvoiceItemDTO> items) { this.items = items; return this; }

        public InvoiceResponseDTO build() {
            return new InvoiceResponseDTO(id, invoiceNumber, invoiceDate, dueDate, subtotal, cgst, sgst, grandTotal, status, selectedTemplate, templateName, notes, createdAt, client, user, items);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getCgst() { return cgst; }
    public void setCgst(BigDecimal cgst) { this.cgst = cgst; }

    public BigDecimal getSgst() { return sgst; }
    public void setSgst(BigDecimal sgst) { this.sgst = sgst; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public Integer getSelectedTemplate() { return selectedTemplate; }
    public void setSelectedTemplate(Integer selectedTemplate) { this.selectedTemplate = selectedTemplate; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ClientDTO getClient() { return client; }
    public void setClient(ClientDTO client) { this.client = client; }

    public UserProfileDTO getUser() { return user; }
    public void setUser(UserProfileDTO user) { this.user = user; }

    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }
}
