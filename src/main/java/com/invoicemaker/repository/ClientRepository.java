package com.invoicemaker.repository;

import com.invoicemaker.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByOrderByCreatedAtDesc();
    boolean existsByClientEmail(String clientEmail);
}
