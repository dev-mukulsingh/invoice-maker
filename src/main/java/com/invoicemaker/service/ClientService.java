package com.invoicemaker.service;

import com.invoicemaker.dto.ClientDTO;
import com.invoicemaker.entity.Client;
import com.invoicemaker.exception.ResourceNotFoundException;
import com.invoicemaker.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        Client client = getClientEntity(id);
        return toDTO(client);
    }

    @Transactional(readOnly = true)
    public Client getClientEntity(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client with ID " + id + " not found"));
    }

    @Transactional
    public ClientDTO createClient(ClientDTO dto) {
        Client client = Client.builder()
                .clientName(dto.getClientName().trim())
                .clientEmail(dto.getClientEmail().trim())
                .clientPhone(dto.getClientPhone() != null ? dto.getClientPhone().trim() : null)
                .billingAddress(dto.getBillingAddress() != null ? dto.getBillingAddress().trim() : null)
                .gstNumber(dto.getGstNumber() != null ? dto.getGstNumber().trim().toUpperCase() : null)
                .build();
        Client saved = clientRepository.save(client);
        return toDTO(saved);
    }

    @Transactional
    public ClientDTO updateClient(Long id, ClientDTO dto) {
        Client client = getClientEntity(id);
        client.setClientName(dto.getClientName().trim());
        client.setClientEmail(dto.getClientEmail().trim());
        client.setClientPhone(dto.getClientPhone() != null ? dto.getClientPhone().trim() : null);
        client.setBillingAddress(dto.getBillingAddress() != null ? dto.getBillingAddress().trim() : null);
        client.setGstNumber(dto.getGstNumber() != null ? dto.getGstNumber().trim().toUpperCase() : null);
        Client updated = clientRepository.save(client);
        return toDTO(updated);
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = getClientEntity(id);
        clientRepository.delete(client);
    }

    public ClientDTO toDTO(Client client) {
        if (client == null) return null;
        return ClientDTO.builder()
                .id(client.getId())
                .clientName(client.getClientName())
                .clientEmail(client.getClientEmail())
                .clientPhone(client.getClientPhone())
                .billingAddress(client.getBillingAddress())
                .gstNumber(client.getGstNumber())
                .createdAt(client.getCreatedAt())
                .build();
    }
}
