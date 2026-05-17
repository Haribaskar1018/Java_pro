package com.splitwisepro.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expense_shares")
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

    public ExpenseShare() {}

    public ExpenseShare(Long id, Expense expense, User user, BigDecimal shareAmount, BigDecimal sharePercentage, boolean settled, String settlementNote) {
        this.id = id;
        this.expense = expense;
        this.user = user;
        this.shareAmount = shareAmount;
        this.sharePercentage = sharePercentage;
        this.settled = settled;
        this.settlementNote = settlementNote;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Expense getExpense() { return expense; }
    public void setExpense(Expense expense) { this.expense = expense; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public BigDecimal getShareAmount() { return shareAmount; }
    public void setShareAmount(BigDecimal shareAmount) { this.shareAmount = shareAmount; }
    public BigDecimal getSharePercentage() { return sharePercentage; }
    public void setSharePercentage(BigDecimal sharePercentage) { this.sharePercentage = sharePercentage; }
    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
    public String getSettlementNote() { return settlementNote; }
    public void setSettlementNote(String settlementNote) { this.settlementNote = settlementNote; }

    public static ExpenseShareBuilder builder() {
        return new ExpenseShareBuilder();
    }

    public static class ExpenseShareBuilder {
        private Long id;
        private Expense expense;
        private User user;
        private BigDecimal shareAmount;
        private BigDecimal sharePercentage;
        private boolean settled;
        private String settlementNote;

        ExpenseShareBuilder() {}

        public ExpenseShareBuilder id(Long id) { this.id = id; return this; }
        public ExpenseShareBuilder expense(Expense expense) { this.expense = expense; return this; }
        public ExpenseShareBuilder user(User user) { this.user = user; return this; }
        public ExpenseShareBuilder shareAmount(BigDecimal shareAmount) { this.shareAmount = shareAmount; return this; }
        public ExpenseShareBuilder sharePercentage(BigDecimal sharePercentage) { this.sharePercentage = sharePercentage; return this; }
        public ExpenseShareBuilder settled(boolean settled) { this.settled = settled; return this; }
        public ExpenseShareBuilder settlementNote(String settlementNote) { this.settlementNote = settlementNote; return this; }

        public ExpenseShare build() {
            return new ExpenseShare(id, expense, user, shareAmount, sharePercentage, settled, settlementNote);
        }
    }
}
