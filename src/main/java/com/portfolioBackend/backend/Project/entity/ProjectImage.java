package com.portfolioBackend.backend.Project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String imageUrl;

    String publicId;

    @Column(name = "display_order", nullable = false)
    Integer displayOrder;

    @ManyToOne
    Project project;
}

