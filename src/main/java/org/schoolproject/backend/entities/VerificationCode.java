package org.schoolproject.backend.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

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
    private int idVerif;

    private String email;

    private String code;

    @CreationTimestamp
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDate updateDate;
}
