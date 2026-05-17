package com.splitwisepro.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expense_shares")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(precision = 10, scale = 2)
    private BigDecimal shareAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal sharePercentage;

    private boolean settled;

    private String settlementNote;
}
