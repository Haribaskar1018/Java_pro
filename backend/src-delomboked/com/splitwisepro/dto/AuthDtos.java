package com.splitwisepro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// ─── Auth DTOs ──────────────────────────────────────────────────
public class AuthDtos {

    @Data
    public static class RegisterRequest {
        @NotBlank private String username;
        @NotBlank @Email private String email;
        @NotBlank @Size(min = 6) private String password;
        private String fullName;
        private String upiId;
        private String phoneNumber;
    }

    @Data
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String username;
        private String email;
        private String fullName;
        private String upiId;
        private Long userId;

        public AuthResponse(String token, Long userId, String username, String email, String fullName, String upiId) {
            this.token = token;
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.fullName = fullName;
            this.upiId = upiId;
        }
    }
}

// ─── Expense DTOs ───────────────────────────────────────────────
class ExpenseDtos {

    @Data
    public static class CreateExpenseRequest {
        @NotBlank private String title;
        private String description;
        private BigDecimal totalAmount;
        private Long groupId;
        private Long paidByUserId;
        private String splitType; // EQUAL, PERCENTAGE, EXACT
        private String category;
        private Map<Long, BigDecimal> customShares; // userId -> amount/percentage (for EXACT/PERCENTAGE)
    }

    @Data
    public static class ExpenseResponse {
        private Long id;
        private String title;
        private BigDecimal totalAmount;
        private String paidByUsername;
        private String splitType;
        private String category;
        private LocalDateTime expenseDate;
        private List<ShareDetail> shares;

        @Data
        public static class ShareDetail {
            private Long userId;
            private String username;
            private BigDecimal shareAmount;
            private boolean settled;
        }
    }
}
