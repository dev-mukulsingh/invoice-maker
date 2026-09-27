package com.invoicemaker.service;

import com.invoicemaker.dto.DashboardStatsDTO;
import com.invoicemaker.dto.InvoiceResponseDTO;
import com.invoicemaker.entity.InvoiceStatus;
import com.invoicemaker.repository.ClientRepository;
import com.invoicemaker.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final InvoiceService invoiceService;

    public DashboardService(InvoiceRepository invoiceRepository,
                            ClientRepository clientRepository,
                            InvoiceService invoiceService) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.invoiceService = invoiceService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        BigDecimal totalRevenue = invoiceRepository.sumGrandTotalByStatus(InvoiceStatus.PAID);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        long totalInvoices = invoiceRepository.count();
        long unpaidInvoices = invoiceRepository.countByStatus(InvoiceStatus.UNPAID);
        long paidInvoices = invoiceRepository.countByStatus(InvoiceStatus.PAID);
        long draftInvoices = invoiceRepository.countByStatus(InvoiceStatus.DRAFT);
        long totalClients = clientRepository.count();

        List<InvoiceResponseDTO> recentInvoices = invoiceRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(invoiceService::toDTO)
                .collect(Collectors.toList());

        return DashboardStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalInvoices(totalInvoices)
                .unpaidInvoices(unpaidInvoices)
                .paidInvoices(paidInvoices)
                .draftInvoices(draftInvoices)
                .totalClients(totalClients)
                .recentInvoices(recentInvoices)
                .build();
    }
}
