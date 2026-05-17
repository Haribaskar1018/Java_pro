package com.splitwisepro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CreateExpenseRequest {
    @NotBlank private String title;
    private String description;
    private BigDecimal totalAmount;
    private Long groupId;
    private String splitType; // EQUAL, PERCENTAGE, EXACT
    private String category;
    private Map<Long, BigDecimal> customShares;
    private List<Long> memberIds;
}

@Data
class CreateGroupRequest {
    @NotBlank private String name;
    private String description;
    private List<Long> memberIds;
}

@Data
class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private String createdBy;
    private List<MemberDto> members;
    private LocalDateTime createdAt;
    private int expenseCount;

    @Data
    static class MemberDto {
        private Long id;
        private String username;
        private String fullName;
        private String upiId;
    }
}

@Data
class ExpenseShareResponse {
    private Long expenseId;
    private String expenseTitle;
    private BigDecimal shareAmount;
    private String paidByUsername;
    private boolean settled;
    private LocalDateTime expenseDate;
}

@Data
class SettlementSummary {
    private Map<String, BigDecimal> owes;
    private Map<String, BigDecimal> isOwed;
    private BigDecimal totalOwed;
    private BigDecimal totalOwing;
}

@Data
class QrRequest {
    private String upiId;
    private String name;
    private BigDecimal amount;
    private String note;
}
