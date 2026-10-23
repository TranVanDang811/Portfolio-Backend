package com.portfolioBackend.backend.controller;

import com.portfolioBackend.backend.Contact.dto.ContactRequest;
import com.portfolioBackend.backend.Contact.entity.Contact;
import com.portfolioBackend.backend.Contact.repository.ContactRepository;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContactController {

    ContactRepository contactRepository;

    @PostMapping("/contacts")
    public ApiResponse<Contact> submitContact(@Valid @RequestBody ContactRequest request) {
        Contact contact = Contact.builder()
                .name(request.getName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .readStatus(false)
                .build();

        Contact saved = contactRepository.save(contact);
        return ApiResponse.<Contact>builder()
                .message("Your message has been sent successfully!")
                .result(saved)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/contacts")
    public ApiResponse<List<Contact>> getAllContacts() {
        return ApiResponse.<List<Contact>>builder()
                .result(contactRepository.findAll())
                .build();
    }
}
