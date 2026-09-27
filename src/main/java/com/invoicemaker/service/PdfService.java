package com.invoicemaker.service;

import com.invoicemaker.entity.Invoice;
import com.invoicemaker.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);

    private final TemplateEngine templateEngine;
    private final InvoiceService invoiceService;

    public PdfService(TemplateEngine templateEngine, InvoiceService invoiceService) {
        this.templateEngine = templateEngine;
        this.invoiceService = invoiceService;
    }

    public byte[] generateInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceService.getInvoiceEntity(invoiceId);
        return generateInvoicePdf(invoice);
    }

    public byte[] generateInvoicePdf(Invoice invoice) {
        try {
            Context context = new Context(Locale.ENGLISH);
            context.setVariable("invoice", invoice);
            context.setVariable("client", invoice.getClient());
            context.setVariable("user", invoice.getUser());
            context.setVariable("items", invoice.getItems());
            context.setVariable("templateId", invoice.getSelectedTemplate() != null ? invoice.getSelectedTemplate() : 1);
            context.setVariable("templateName", InvoiceService.TEMPLATE_NAMES.getOrDefault(invoice.getSelectedTemplate(), "Classic Ghibli Parchment"));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
            context.setVariable("formattedInvoiceDate", invoice.getInvoiceDate() != null ? invoice.getInvoiceDate().format(dtf) : "");
            context.setVariable("formattedDueDate", invoice.getDueDate() != null ? invoice.getDueDate().format(dtf) : "");

            String htmlContent = templateEngine.process("pdf/invoice-template", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF for invoice {}", invoice.getInvoiceNumber(), e);
            throw new BadRequestException("PDF generation failed: " + e.getMessage());
        }
    }
}
