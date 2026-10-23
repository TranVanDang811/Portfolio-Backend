package com.portfolioBackend.backend.Testimonial.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "testimonials")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, length = 150)
    String name;

    @Column(nullable = false, length = 200)
    String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    String review;

    @Column(length = 500)
    String avatarUrl;

    @Column(nullable = false)
    Integer displayOrder;
}
