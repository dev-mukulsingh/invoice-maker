package com.invoicemaker.service;

import com.invoicemaker.dto.InvoiceItemDTO;
import com.invoicemaker.dto.InvoiceRequestDTO;
import com.invoicemaker.dto.InvoiceResponseDTO;
import com.invoicemaker.entity.Client;
import com.invoicemaker.entity.Invoice;
import com.invoicemaker.entity.InvoiceItem;
import com.invoicemaker.entity.InvoiceStatus;
import com.invoicemaker.entity.User;
import com.invoicemaker.exception.BadRequestException;
import com.invoicemaker.exception.ResourceNotFoundException;
import com.invoicemaker.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientService clientService;
    private final UserService userService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          ClientService clientService,
                          UserService userService) {
        this.invoiceRepository = invoiceRepository;
        this.clientService = clientService;
        this.userService = userService;
    }

    public static final Map<Integer, String> TEMPLATE_NAMES = Map.of(
            1, "Classic Ghibli Parchment",
            2, "Vintage Ledger",
            3, "Cozy Cottage",
            4, "Tokyo Monospace",
            5, "Sunset Terracotta",
            6, "Pastel Kraft",
            7, "Japanese Hanko Stamp",
            8, "Artisan Blueprint",
            9, "Editorial Notebook",
            10, "Modern Minimal Chic"
    );

    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long id) {
        Invoice invoice = getInvoiceEntity(id);
        return toDTO(invoice);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoiceEntity(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice with ID " + id + " not found"));
    }

    @Transactional
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BadRequestException("Invoice must contain at least one line item");
        }

        Client client = clientService.getClientEntity(dto.getClientId());
        User user = userService.getOrCreateDefaultUser();

        String invoiceNumber = dto.getInvoiceNumber();
        if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
            invoiceNumber = generateUniqueInvoiceNumber();
        } else {
            invoiceNumber = invoiceNumber.trim().toUpperCase();
            if (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
                throw new BadRequestException("Invoice number '" + invoiceNumber + "' already exists");
            }
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .invoiceDate(dto.getInvoiceDate() != null ? dto.getInvoiceDate() : LocalDate.now())
                .dueDate(dto.getDueDate() != null ? dto.getDueDate() : LocalDate.now().plusDays(15))
                .status(dto.getStatus() != null ? dto.getStatus() : InvoiceStatus.UNPAID)
                .selectedTemplate(dto.getSelectedTemplate() != null ? dto.getSelectedTemplate() : 1)
                .notes(dto.getNotes())
                .client(client)
                .user(user)
                .items(new ArrayList<>())
                .build();

        // Calculate line items and totals
        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItemDTO itemDto : dto.getItems()) {
            BigDecimal qty = BigDecimal.valueOf(itemDto.getQuantity());
            BigDecimal unitPrice = itemDto.getUnitPrice().setScale(2, RoundingMode.HALF_UP);
            BigDecimal itemTotal = qty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(itemTotal);

            InvoiceItem item = InvoiceItem.builder()
                    .description(itemDto.getDescription().trim())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(unitPrice)
                    .taxRate(itemDto.getTaxRate() != null ? itemDto.getTaxRate() : new BigDecimal("18.00"))
                    .itemTotal(itemTotal)
                    .build();

            invoice.addItem(item);
        }

        // 9% CGST + 9% SGST = 18% Total GST
        BigDecimal cgst = subtotal.multiply(new BigDecimal("0.09")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sgst = subtotal.multiply(new BigDecimal("0.09")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = subtotal.add(cgst).add(sgst).setScale(2, RoundingMode.HALF_UP);

        invoice.setSubtotal(subtotal);
        invoice.setCgst(cgst);
        invoice.setSgst(sgst);
        invoice.setGrandTotal(grandTotal);

        Invoice saved = invoiceRepository.save(invoice);
        return toDTO(saved);
    }

    @Transactional
    public InvoiceResponseDTO updateInvoiceStatus(Long id, InvoiceStatus status) {
        Invoice invoice = getInvoiceEntity(id);
        invoice.setStatus(status);
        Invoice updated = invoiceRepository.save(invoice);
        return toDTO(updated);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = getInvoiceEntity(id);
        invoiceRepository.delete(invoice);
    }

    public synchronized String generateUniqueInvoiceNumber() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        String invoiceNumber;
        do {
            int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
            invoiceNumber = "INV-" + year + "-" + randomNum;
        } while (invoiceRepository.existsByInvoiceNumber(invoiceNumber));
        return invoiceNumber;
    }

    public InvoiceResponseDTO toDTO(Invoice invoice) {
        if (invoice == null) return null;

        List<InvoiceItemDTO> itemDTOs = invoice.getItems().stream()
                .map(item -> InvoiceItemDTO.builder()
                        .id(item.getId())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .taxRate(item.getTaxRate())
                        .itemTotal(item.getItemTotal())
                        .build())
                .collect(Collectors.toList());

        String templateName = TEMPLATE_NAMES.getOrDefault(invoice.getSelectedTemplate(), "Classic Ghibli Parchment");

        return InvoiceResponseDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .subtotal(invoice.getSubtotal())
                .cgst(invoice.getCgst())
                .sgst(invoice.getSgst())
                .grandTotal(invoice.getGrandTotal())
                .status(invoice.getStatus())
                .selectedTemplate(invoice.getSelectedTemplate())
                .templateName(templateName)
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                .client(clientService.toDTO(invoice.getClient()))
                .user(userService.toDTO(invoice.getUser()))
                .items(itemDTOs)
                .build();
    }
}
