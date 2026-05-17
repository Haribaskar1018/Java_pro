package com.splitwisepro.service;

import com.splitwisepro.dto.CreateExpenseRequest;
import com.splitwisepro.exception.BadRequestException;
import com.splitwisepro.exception.ResourceNotFoundException;
import com.splitwisepro.model.*;
import com.splitwisepro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ExpenseShareRepository shareRepository;

    @Transactional
    public Expense createExpense(CreateExpenseRequest req, User currentUser) {
        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        User paidBy = currentUser; // always the logged-in user pays

        List<User> members = req.getMemberIds() != null
                ? userRepository.findAllById(req.getMemberIds())
                : group.getMembers();

        if (members.isEmpty()) throw new BadRequestException("No members to split with");

        Expense expense = Expense.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .totalAmount(req.getTotalAmount())
                .paidBy(paidBy)
                .group(group)
                .splitType(Expense.SplitType.valueOf(req.getSplitType()))
                .category(req.getCategory())
                .build();

        expense = expenseRepository.save(expense);

        List<ExpenseShare> shares = calculateShares(expense, members, req);
        shareRepository.saveAll(shares);
        expense.setShares(shares);

        return expense;
    }

    private List<ExpenseShare> calculateShares(Expense expense, List<User> members,
                                                CreateExpenseRequest req) {
        List<ExpenseShare> shares = new ArrayList<>();
        BigDecimal total = expense.getTotalAmount();
        int count = members.size();

        switch (expense.getSplitType()) {
            case EQUAL -> {
                BigDecimal share = total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                for (User member : members) {
                    shares.add(ExpenseShare.builder()
                            .expense(expense)
                            .user(member)
                            .shareAmount(share)
                            .settled(member.getId().equals(expense.getPaidBy().getId()))
                            .build());
                }
            }
            case PERCENTAGE -> {
                if (req.getCustomShares() == null) throw new BadRequestException("Percentages required for PERCENTAGE split");
                for (User member : members) {
                    BigDecimal pct = req.getCustomShares().getOrDefault(member.getId(), BigDecimal.ZERO);
                    BigDecimal amount = total.multiply(pct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    shares.add(ExpenseShare.builder()
                            .expense(expense)
                            .user(member)
                            .shareAmount(amount)
                            .sharePercentage(pct)
                            .settled(member.getId().equals(expense.getPaidBy().getId()))
                            .build());
                }
            }
            case EXACT -> {
                if (req.getCustomShares() == null) throw new BadRequestException("Amounts required for EXACT split");
                for (User member : members) {
                    BigDecimal amount = req.getCustomShares().getOrDefault(member.getId(), BigDecimal.ZERO);
                    shares.add(ExpenseShare.builder()
                            .expense(expense)
                            .user(member)
                            .shareAmount(amount)
                            .settled(member.getId().equals(expense.getPaidBy().getId()))
                            .build());
                }
            }
        }
        return shares;
    }

    public List<Expense> getGroupExpenses(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        return expenseRepository.findByGroup(group);
    }

    public Map<String, Object> getSettlementSummary(User user) {
        List<ExpenseShare> unsettled = shareRepository.findByUserAndSettled(user, false);

        BigDecimal totalOwing = BigDecimal.ZERO;
        BigDecimal totalOwed = BigDecimal.ZERO;
        Map<String, BigDecimal> owes = new HashMap<>();
        Map<String, BigDecimal> isOwed = new HashMap<>();

        for (ExpenseShare share : unsettled) {
            User paidBy = share.getExpense().getPaidBy();
            if (!paidBy.getId().equals(user.getId())) {
                // Current user owes money to paidBy
                totalOwing = totalOwing.add(share.getShareAmount());
                owes.merge(paidBy.getUsername(), share.getShareAmount(), BigDecimal::add);
            }
        }

        // What others owe user
        List<Expense> paidExpenses = expenseRepository.findByPaidBy(user);
        for (Expense exp : paidExpenses) {
            for (ExpenseShare share : exp.getShares()) {
                if (!share.getUser().getId().equals(user.getId()) && !share.isSettled()) {
                    totalOwed = totalOwed.add(share.getShareAmount());
                    isOwed.merge(share.getUser().getUsername(), share.getShareAmount(), BigDecimal::add);
                }
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("owes", owes);
        summary.put("isOwed", isOwed);
        summary.put("totalOwing", totalOwing);
        summary.put("totalOwed", totalOwed);
        summary.put("netBalance", totalOwed.subtract(totalOwing));
        return summary;
    }

    @Transactional
    public void settleShare(Long shareId, User currentUser) {
        ExpenseShare share = shareRepository.findById(shareId)
                .orElseThrow(() -> new ResourceNotFoundException("Share not found"));
        share.setSettled(true);
        share.setSettlementNote("Settled via SplitWise Pro");
        shareRepository.save(share);
    }
}
