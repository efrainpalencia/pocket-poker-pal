package com.pocket_poker_pal_app.PocketPokerPalApp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Table(name = "rulebooks")
@Data
public class RulebookEntity {

    @Id
    private UUID id;

    private String title;

    private String version;

    @Enumerated(EnumType.STRING)
    private Source source; // TDA or SEMINOLE

    private LocalDateTime uploadDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private AdminUser uploadedBy;

    // Enum
    public enum Source {
        TDA, SEMINOLE
    }
}
