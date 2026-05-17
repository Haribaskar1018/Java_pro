package com.splitwisepro.controller;

import com.splitwisepro.dto.ExpenseDtos.CreateExpenseRequest;
import com.splitwisepro.model.Expense;
import com.splitwisepro.model.User;
import com.splitwisepro.repository.UserRepository;
import com.splitwisepro.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Bill splitting & expense management")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }


    @PostMapping
    @Operation(summary = "Create and split an expense")
    public ResponseEntity<Expense> createExpense(@RequestBody CreateExpenseRequest request,
                                                  Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(expenseService.createExpense(request, user));
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get all expenses for a group")
    public ResponseEntity<List<Expense>> getGroupExpenses(@PathVariable Long groupId) {
        return ResponseEntity.ok(expenseService.getGroupExpenses(groupId));
    }

    @GetMapping("/settlement-summary")
    @Operation(summary = "Get settlement summary for logged-in user")
    public ResponseEntity<Map<String, Object>> getSettlementSummary(Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(expenseService.getSettlementSummary(user));
    }

    @PatchMapping("/settle/{shareId}")
    @Operation(summary = "Mark a share as settled")
    public ResponseEntity<Map<String, String>> settleShare(@PathVariable Long shareId,
                                                            Authentication auth) {
        expenseService.settleShare(shareId, getUser(auth));
        return ResponseEntity.ok(Map.of("message", "Share settled successfully"));
    }

    private User getUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
