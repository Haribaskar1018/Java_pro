package com.splitwisepro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ExpenseDtos {

    public static class CreateExpenseRequest {
        @NotBlank private String title;
        private String description;
        private BigDecimal totalAmount;
        private Long groupId;
        private String splitType; // EQUAL, PERCENTAGE, EXACT
        private String category;
        private Map<Long, BigDecimal> customShares;
        private List<Long> memberIds;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public Long getGroupId() { return groupId; }
        public void setGroupId(Long groupId) { this.groupId = groupId; }
        public String getSplitType() { return splitType; }
        public void setSplitType(String splitType) { this.splitType = splitType; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Map<Long, BigDecimal> getCustomShares() { return customShares; }
        public void setCustomShares(Map<Long, BigDecimal> customShares) { this.customShares = customShares; }
        public List<Long> getMemberIds() { return memberIds; }
        public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }
    }

    public static class CreateGroupRequest {
        @NotBlank private String name;
        private String description;
        private List<Long> memberIds;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<Long> getMemberIds() { return memberIds; }
        public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }
    }

    public static class GroupResponse {
        private Long id;
        private String name;
        private String description;
        private String createdBy;
        private List<MemberDto> members;
        private LocalDateTime createdAt;
        private int expenseCount;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public List<MemberDto> getMembers() { return members; }
        public void setMembers(List<MemberDto> members) { this.members = members; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public int getExpenseCount() { return expenseCount; }
        public void setExpenseCount(int expenseCount) { this.expenseCount = expenseCount; }

        public static class MemberDto {
            private Long id;
            private String username;
            private String fullName;
            private String upiId;

            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }
            public String getFullName() { return fullName; }
            public void setFullName(String fullName) { this.fullName = fullName; }
            public String getUpiId() { return upiId; }
            public void setUpiId(String upiId) { this.upiId = upiId; }
        }
    }

    public static class ExpenseShareResponse {
        private Long expenseId;
        private String expenseTitle;
        private BigDecimal shareAmount;
        private String paidByUsername;
        private boolean settled;
        private LocalDateTime expenseDate;

        public Long getExpenseId() { return expenseId; }
        public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
        public String getExpenseTitle() { return expenseTitle; }
        public void setExpenseTitle(String expenseTitle) { this.expenseTitle = expenseTitle; }
        public BigDecimal getShareAmount() { return shareAmount; }
        public void setShareAmount(BigDecimal shareAmount) { this.shareAmount = shareAmount; }
        public String getPaidByUsername() { return paidByUsername; }
        public void setPaidByUsername(String paidByUsername) { this.paidByUsername = paidByUsername; }
        public boolean isSettled() { return settled; }
        public void setSettled(boolean settled) { this.settled = settled; }
        public LocalDateTime getExpenseDate() { return expenseDate; }
        public void setExpenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; }
    }

    public static class SettlementSummary {
        private Map<String, BigDecimal> owes;
        private Map<String, BigDecimal> isOwed;
        private BigDecimal totalOwed;
        private BigDecimal totalOwing;

        public Map<String, BigDecimal> getOwes() { return owes; }
        public void setOwes(Map<String, BigDecimal> owes) { this.owes = owes; }
        public Map<String, BigDecimal> getIsOwed() { return isOwed; }
        public void setIsOwed(Map<String, BigDecimal> isOwed) { this.isOwed = isOwed; }
        public BigDecimal getTotalOwed() { return totalOwed; }
        public void setTotalOwed(BigDecimal totalOwed) { this.totalOwed = totalOwed; }
        public BigDecimal getTotalOwing() { return totalOwing; }
        public void setTotalOwing(BigDecimal totalOwing) { this.totalOwing = totalOwing; }
    }

    public static class QrRequest {
        private String upiId;
        private String name;
        private BigDecimal amount;
        private String note;

        public String getUpiId() { return upiId; }
        public void setUpiId(String upiId) { this.upiId = upiId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }
}
