package com.invoicemaker.service;

import com.invoicemaker.dto.InvoiceItemDTO;
import com.invoicemaker.dto.InvoiceRequestDTO;
import com.invoicemaker.entity.Client;
import com.invoicemaker.entity.InvoiceStatus;
import com.invoicemaker.entity.User;
import com.invoicemaker.repository.ClientRepository;
import com.invoicemaker.repository.InvoiceRepository;
import com.invoicemaker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    public DataInitializer(UserRepository userRepository,
                           ClientRepository clientRepository,
                           InvoiceRepository invoiceRepository,
                           InvoiceService invoiceService) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
    }

    @Override
    public void run(String... args) {
        // Seed default user if none exists
        User user = userRepository.findFirstByOrderByIdAsc().orElseGet(() -> {
            log.info("Seeding default business user profile...");
            User newUser = User.builder()
                    .businessName("Studio Tonari Creative Workshop")
                    .email("billing@tonari-workshop.com")
                    .phone("+91 98765 43210")
                    .gstNumber("27AABCT3518Q1ZY")
                    .address("404 Windmill Valley, Hinoki District, Pune 411001, MH, India")
                    .build();
            return userRepository.save(newUser);
        });

        // Seed clients if none exist
        if (clientRepository.count() == 0) {
            log.info("Seeding sample clients...");
            Client c1 = clientRepository.save(Client.builder()
                    .clientName("Kiki's Flying Delivery Service")
                    .clientEmail("kiki@koriko-bakery.com")
                    .clientPhone("+91 98111 22233")
                    .billingAddress("7 Osono Bakery Lane, Koriko Harbor, MH")
                    .gstNumber("27AAACK1010A1Z1")
                    .build());

            Client c2 = clientRepository.save(Client.builder()
                    .clientName("Howl's Moving Architecture")
                    .clientEmail("howl@pendragon-studio.org")
                    .clientPhone("+91 98222 33344")
                    .billingAddress("P.O. Box 9, Waste Citadel, Maharashtra")
                    .gstNumber("27AAACH2020B2Z2")
                    .build());

            Client c3 = clientRepository.save(Client.builder()
                    .clientName("Aburaya Spirited Onsen Resort")
                    .clientEmail("accounts@aburaya-bathhouse.jp")
                    .clientPhone("+91 98333 44455")
                    .billingAddress("Bridge Gate 1, Spirit Realm Way, MH")
                    .gstNumber("27AAACA3030C3Z3")
                    .build());

            Client c4 = clientRepository.save(Client.builder()
                    .clientName("Laputa Ancient Aerodynamics")
                    .clientEmail("contact@laputa-foundry.net")
                    .clientPhone("+91 98444 55566")
                    .billingAddress("Castle Tier 3, Floating Cloud Isle, MH")
                    .gstNumber("27AAACL4040D4Z4")
                    .build());

            // Seed sample invoices
            if (invoiceRepository.count() == 0) {
                log.info("Seeding initial invoices with retro templates...");

                // Invoice 1 - Template 1 (Classic Ghibli Parchment) - PAID
                invoiceService.createInvoice(InvoiceRequestDTO.builder()
                        .invoiceNumber("INV-2026-1001")
                        .clientId(c1.getId())
                        .invoiceDate(LocalDate.now().minusDays(10))
                        .dueDate(LocalDate.now().plusDays(5))
                        .selectedTemplate(1)
                        .status(InvoiceStatus.PAID)
                        .notes("Thank you for choosing Studio Tonari! Deliveries packed with utmost care.")
                        .items(List.of(
                                InvoiceItemDTO.builder().description("Artisan Herb Baguette Delivery (50 pkts)").quantity(50).unitPrice(new BigDecimal("120.00")).build(),
                                InvoiceItemDTO.builder().description("Handcrafted Wooden Delivery Hampers").quantity(5).unitPrice(new BigDecimal("950.00")).build(),
                                InvoiceItemDTO.builder().description("Express Skyway Broom Courier Surcharge").quantity(1).unitPrice(new BigDecimal("1500.00")).build()
                        ))
                        .build());

                // Invoice 2 - Template 4 (Tokyo Monospace) - UNPAID
                invoiceService.createInvoice(InvoiceRequestDTO.builder()
                        .invoiceNumber("INV-2026-1002")
                        .clientId(c2.getId())
                        .invoiceDate(LocalDate.now().minusDays(4))
                        .dueDate(LocalDate.now().plusDays(10))
                        .selectedTemplate(4)
                        .status(InvoiceStatus.UNPAID)
                        .notes("Payment terms: 15 days net. Please wire to registered bank coordinates.")
                        .items(List.of(
                                InvoiceItemDTO.builder().description("Steam Valve Overhaul & Gear Calibration").quantity(1).unitPrice(new BigDecimal("18500.00")).build(),
                                InvoiceItemDTO.builder().description("Dimensional Hearth Fire Retardant Bricks").quantity(40).unitPrice(new BigDecimal("350.00")).build()
                        ))
                        .build());

                // Invoice 3 - Template 7 (Japanese Hanko Stamp) - PAID
                invoiceService.createInvoice(InvoiceRequestDTO.builder()
                        .invoiceNumber("INV-2026-1003")
                        .clientId(c3.getId())
                        .invoiceDate(LocalDate.now().minusDays(8))
                        .dueDate(LocalDate.now().plusDays(7))
                        .selectedTemplate(7)
                        .status(InvoiceStatus.PAID)
                        .notes("All herbal extracts certified organic. Hanko seal verified upon delivery.")
                        .items(List.of(
                                InvoiceItemDTO.builder().description("Herbal Herbal Infused Mineral Salts (25kg)").quantity(4).unitPrice(new BigDecimal("3200.00")).build(),
                                InvoiceItemDTO.builder().description("Aromatic Hinoki Wooden Bath Ladles").quantity(15).unitPrice(new BigDecimal("450.00")).build(),
                                InvoiceItemDTO.builder().description("River Spirit Purification Incense Bales").quantity(10).unitPrice(new BigDecimal("800.00")).build()
                        ))
                        .build());

                // Invoice 4 - Template 8 (Artisan Blueprint) - UNPAID
                invoiceService.createInvoice(InvoiceRequestDTO.builder()
                        .invoiceNumber("INV-2026-1004")
                        .clientId(c4.getId())
                        .invoiceDate(LocalDate.now().minusDays(2))
                        .dueDate(LocalDate.now().plusDays(14))
                        .selectedTemplate(8)
                        .status(InvoiceStatus.UNPAID)
                        .notes("Blueprint revision C-12 approved. Retain copy for engineering compliance.")
                        .items(List.of(
                                InvoiceItemDTO.builder().description("Levitation Field Harmonic Calibration").quantity(1).unitPrice(new BigDecimal("42000.00")).build(),
                                InvoiceItemDTO.builder().description("Aero-grade Copper Conductor Bundles").quantity(6).unitPrice(new BigDecimal("2800.00")).build()
                        ))
                        .build());
            }
        }
    }
}
