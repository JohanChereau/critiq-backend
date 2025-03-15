package com.johanchereau.critiqbackend.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime createdDate;
    private LocalDateTime expiresDate;
    private LocalDateTime validatedDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
