package org.schoolproject.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_verif", nullable = false, updatable = false)
    private Integer idVerif;

    @NotNull
    @Column(nullable = false)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String code;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NonNull
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}