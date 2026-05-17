package com.splitwisepro.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "paid_by")
    private User paidBy;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private SplitType splitType; // EQUAL, PERCENTAGE, EXACT

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExpenseShare> shares;

    private String category; // food, travel, utilities, etc.

    private LocalDateTime expenseDate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.expenseDate == null) this.expenseDate = LocalDateTime.now();
    }

    public Expense() {}

    public Expense(Long id, String title, String description, BigDecimal totalAmount, User paidBy, Group group, SplitType splitType, List<ExpenseShare> shares, String category, LocalDateTime expenseDate, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.totalAmount = totalAmount;
        this.paidBy = paidBy;
        this.group = group;
        this.splitType = splitType;
        this.shares = shares;
        this.category = category;
        this.expenseDate = expenseDate;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public User getPaidBy() { return paidBy; }
    public void setPaidBy(User paidBy) { this.paidBy = paidBy; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public SplitType getSplitType() { return splitType; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public List<ExpenseShare> getShares() { return shares; }
    public void setShares(List<ExpenseShare> shares) { this.shares = shares; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ExpenseBuilder builder() {
        return new ExpenseBuilder();
    }

    public static class ExpenseBuilder {
        private Long id;
        private String title;
        private String description;
        private BigDecimal totalAmount;
        private User paidBy;
        private Group group;
        private SplitType splitType;
        private List<ExpenseShare> shares;
        private String category;
        private LocalDateTime expenseDate;
        private LocalDateTime createdAt;

        ExpenseBuilder() {}

        public ExpenseBuilder id(Long id) { this.id = id; return this; }
        public ExpenseBuilder title(String title) { this.title = title; return this; }
        public ExpenseBuilder description(String description) { this.description = description; return this; }
        public ExpenseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public ExpenseBuilder paidBy(User paidBy) { this.paidBy = paidBy; return this; }
        public ExpenseBuilder group(Group group) { this.group = group; return this; }
        public ExpenseBuilder splitType(SplitType splitType) { this.splitType = splitType; return this; }
        public ExpenseBuilder shares(List<ExpenseShare> shares) { this.shares = shares; return this; }
        public ExpenseBuilder category(String category) { this.category = category; return this; }
        public ExpenseBuilder expenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; return this; }
        public ExpenseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Expense build() {
            return new Expense(id, title, description, totalAmount, paidBy, group, splitType, shares, category, expenseDate, createdAt);
        }
    }

    public enum SplitType {
        EQUAL, PERCENTAGE, EXACT
    }
}
