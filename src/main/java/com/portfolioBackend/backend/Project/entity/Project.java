package com.portfolioBackend.backend.Project.entity;

import com.portfolioBackend.backend.ProjectCategory.entity.ProjectCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String title;

    String slug;

    String thumbnailUrl;

    String githubUrl;

    String demoUrl;

    Boolean featured;

    @Builder.Default
    Long viewCount = 0L;

    @Column(columnDefinition = "TEXT")
    String shortDescription;

    @Column(columnDefinition = "LONGTEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String technologies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id"
    )
    private ProjectCategory category;

    @OneToMany(mappedBy = "project",
            cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProjectImage> images;
}