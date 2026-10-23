package com.portfolioBackend.backend.Contact.repository;

import com.portfolioBackend.backend.Contact.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, String> {
}