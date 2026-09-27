package com.invoicemaker;

import com.invoicemaker.dto.InvoiceItemDTO;
import com.invoicemaker.dto.InvoiceRequestDTO;
import com.invoicemaker.dto.InvoiceResponseDTO;
import com.invoicemaker.entity.Client;
import com.invoicemaker.entity.InvoiceStatus;
import com.invoicemaker.repository.ClientRepository;
import com.invoicemaker.service.InvoiceService;
import com.invoicemaker.service.PdfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class InvoiceMakerApplicationTests {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void contextLoads() {
        assertNotNull(invoiceService);
        assertNotNull(pdfService);
    }

    @Test
    void testCreateInvoiceAndGeneratePdfAcrossTemplates() {
        Client client = clientRepository.findAll().stream().findFirst().orElseGet(() ->
                clientRepository.save(Client.builder()
                        .clientName("Test Studio Client")
                        .clientEmail("test@studioclient.org")
                        .billingAddress("123 Cherry Lane")
                        .build())
        );

        for (int templateId = 1; templateId <= 10; templateId++) {
            InvoiceRequestDTO request = InvoiceRequestDTO.builder()
                    .clientId(client.getId())
                    .invoiceDate(LocalDate.now())
                    .dueDate(LocalDate.now().plusDays(10))
                    .selectedTemplate(templateId)
                    .status(templateId % 2 == 0 ? InvoiceStatus.PAID : InvoiceStatus.UNPAID)
                    .notes("Test notes for template #" + templateId)
                    .items(List.of(
                            InvoiceItemDTO.builder().description("Artisan Design Item").quantity(2).unitPrice(new BigDecimal("1500.00")).build(),
                            InvoiceItemDTO.builder().description("Courier Service").quantity(1).unitPrice(new BigDecimal("250.00")).build()
                    ))
                    .build();

            InvoiceResponseDTO created = invoiceService.createInvoice(request);
            assertNotNull(created.getId());
            assertEquals(new BigDecimal("3250.00"), created.getSubtotal());
            // 9% of 3250 = 292.50
            assertEquals(new BigDecimal("292.50"), created.getCgst());
            assertEquals(new BigDecimal("292.50"), created.getSgst());
            // Grand total = 3250 + 292.50 + 292.50 = 3835.00
            assertEquals(new BigDecimal("3835.00"), created.getGrandTotal());

            // Test PDF Generation for this template
            byte[] pdfBytes = pdfService.generateInvoicePdf(created.getId());
            assertNotNull(pdfBytes);
            assertTrue(pdfBytes.length > 500, "PDF should contain valid byte content");
        }
    }
}
