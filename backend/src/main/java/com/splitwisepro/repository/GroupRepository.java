package com.splitwisepro.repository;

import com.splitwisepro.model.Group;
import com.splitwisepro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g WHERE g.createdBy = :user OR :user MEMBER OF g.members")
    List<Group> findAllByMember(User user);

    List<Group> findByCreatedBy(User user);
}
