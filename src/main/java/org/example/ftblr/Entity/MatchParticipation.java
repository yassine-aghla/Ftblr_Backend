package org.example.ftblr.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_participations", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_user_match",
                columnNames = {"user_id", "match_id"}
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    @Column(nullable = false)
    private Boolean isPaid;

    @Column
    private LocalDateTime paymentDate;

    @Column
    private String paymentReference;

    @Column
    private Double amountPaid;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ParticipationStatus.CONFIRMED;
        }
        if (isPaid == null) {
            isPaid = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ParticipationStatus.CANCELLED;
    }

    public void markAsPaid(String reference) {
        this.isPaid = true;
        this.paymentDate = LocalDateTime.now();
        this.paymentReference = reference;
        this.amountPaid = this.match.getCout().doubleValue();
    }
}