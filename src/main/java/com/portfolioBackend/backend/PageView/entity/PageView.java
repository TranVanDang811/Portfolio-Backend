package com.portfolioBackend.backend.PageView.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "page_views",
        indexes = {
                @Index(name = "idx_page_views_visited_at", columnList = "visited_at"),
                @Index(name = "idx_page_views_page", columnList = "page"),
                @Index(name = "idx_page_views_session", columnList = "session_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Column(nullable = false, length = 255)
    private String page;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(length = 500)
    private String referrer;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;
}
