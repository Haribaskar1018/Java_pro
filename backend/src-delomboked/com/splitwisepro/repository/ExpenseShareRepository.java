package com.splitwisepro.repository;

import com.splitwisepro.model.ExpenseShare;
import com.splitwisepro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {

    List<ExpenseShare> findByUser(User user);

    List<ExpenseShare> findByUserAndSettled(User user, boolean settled);

    @Query("SELECT COALESCE(SUM(s.shareAmount), 0) FROM ExpenseShare s WHERE s.user = :user AND s.settled = false")
    BigDecimal getTotalUnsettledAmountForUser(User user);
}
