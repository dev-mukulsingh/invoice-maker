package com.invoicemaker.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDTO {
    private BigDecimal totalRevenue;
    private Long totalInvoices;
    private Long unpaidInvoices;
    private Long paidInvoices;
    private Long draftInvoices;
    private Long totalClients;
    private List<InvoiceResponseDTO> recentInvoices = new ArrayList<>();

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(BigDecimal totalRevenue, Long totalInvoices, Long unpaidInvoices, Long paidInvoices,
                             Long draftInvoices, Long totalClients, List<InvoiceResponseDTO> recentInvoices) {
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalInvoices = totalInvoices != null ? totalInvoices : 0L;
        this.unpaidInvoices = unpaidInvoices != null ? unpaidInvoices : 0L;
        this.paidInvoices = paidInvoices != null ? paidInvoices : 0L;
        this.draftInvoices = draftInvoices != null ? draftInvoices : 0L;
        this.totalClients = totalClients != null ? totalClients : 0L;
        this.recentInvoices = recentInvoices != null ? recentInvoices : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal totalRevenue;
        private Long totalInvoices;
        private Long unpaidInvoices;
        private Long paidInvoices;
        private Long draftInvoices;
        private Long totalClients;
        private List<InvoiceResponseDTO> recentInvoices = new ArrayList<>();

        public Builder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public Builder totalInvoices(Long totalInvoices) { this.totalInvoices = totalInvoices; return this; }
        public Builder unpaidInvoices(Long unpaidInvoices) { this.unpaidInvoices = unpaidInvoices; return this; }
        public Builder paidInvoices(Long paidInvoices) { this.paidInvoices = paidInvoices; return this; }
        public Builder draftInvoices(Long draftInvoices) { this.draftInvoices = draftInvoices; return this; }
        public Builder totalClients(Long totalClients) { this.totalClients = totalClients; return this; }
        public Builder recentInvoices(List<InvoiceResponseDTO> recentInvoices) { this.recentInvoices = recentInvoices; return this; }

        public DashboardStatsDTO build() {
            return new DashboardStatsDTO(totalRevenue, totalInvoices, unpaidInvoices, paidInvoices, draftInvoices, totalClients, recentInvoices);
        }
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Long getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(Long totalInvoices) { this.totalInvoices = totalInvoices; }

    public Long getUnpaidInvoices() { return unpaidInvoices; }
    public void setUnpaidInvoices(Long unpaidInvoices) { this.unpaidInvoices = unpaidInvoices; }

    public Long getPaidInvoices() { return paidInvoices; }
    public void setPaidInvoices(Long paidInvoices) { this.paidInvoices = paidInvoices; }

    public Long getDraftInvoices() { return draftInvoices; }
    public void setDraftInvoices(Long draftInvoices) { this.draftInvoices = draftInvoices; }

    public Long getTotalClients() { return totalClients; }
    public void setTotalClients(Long totalClients) { this.totalClients = totalClients; }

    public List<InvoiceResponseDTO> getRecentInvoices() { return recentInvoices; }
    public void setRecentInvoices(List<InvoiceResponseDTO> recentInvoices) { this.recentInvoices = recentInvoices; }
}
