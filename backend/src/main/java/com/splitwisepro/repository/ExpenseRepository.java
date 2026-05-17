package com.splitwisepro.repository;

import com.splitwisepro.model.Expense;
import com.splitwisepro.model.Group;
import com.splitwisepro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByGroup(Group group);

    List<Expense> findByPaidBy(User user);

    @Query("SELECT e FROM Expense e JOIN e.shares s WHERE s.user = :user AND s.settled = false")
    List<Expense> findUnsettledExpensesByUser(User user);

    @Query("SELECT e FROM Expense e JOIN e.shares s WHERE s.user = :user")
    List<Expense> findAllExpensesByUser(User user);
}
