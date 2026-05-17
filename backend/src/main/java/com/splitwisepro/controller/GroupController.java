package com.splitwisepro.controller;

import com.splitwisepro.exception.ResourceNotFoundException;
import com.splitwisepro.model.Group;
import com.splitwisepro.model.User;
import com.splitwisepro.repository.GroupRepository;
import com.splitwisepro.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Groups", description = "Group management APIs")
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupController(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }


    @PostMapping
    @Operation(summary = "Create a new split group")
    public ResponseEntity<Group> createGroup(@RequestBody CreateGroupReq req, Authentication auth) {
        User currentUser = getUser(auth);
        List<User> members = userRepository.findAllById(req.getMemberIds());
        if (!members.contains(currentUser)) members.add(currentUser);

        Group group = Group.builder()
                .name(req.getName())
                .description(req.getDescription())
                .createdBy(currentUser)
                .members(members)
                .build();

        return ResponseEntity.ok(groupRepository.save(group));
    }

    @GetMapping
    @Operation(summary = "Get all groups for current user")
    public ResponseEntity<List<Group>> getMyGroups(Authentication auth) {
        User user = getUser(auth);
        return ResponseEntity.ok(groupRepository.findAllByMember(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get group by ID")
    public ResponseEntity<Group> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found")));
    }

    @GetMapping("/users/search")
    @Operation(summary = "Search users to add to group")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .filter(u -> u.getUsername().contains(query) || u.getEmail().contains(query))
                .limit(10)
                .toList());
    }

    private User getUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    static class CreateGroupReq {
        private String name;
        private String description;
        private List<Long> memberIds;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<Long> getMemberIds() { return memberIds; }
        public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }
    }
}
