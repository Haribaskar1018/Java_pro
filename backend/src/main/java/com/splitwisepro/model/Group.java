package com.splitwisepro.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "split_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Group() {}

    public Group(Long id, String name, String description, User createdBy, List<User> members, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.members = members;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static GroupBuilder builder() {
        return new GroupBuilder();
    }

    public static class GroupBuilder {
        private Long id;
        private String name;
        private String description;
        private User createdBy;
        private List<User> members;
        private LocalDateTime createdAt;

        GroupBuilder() {}

        public GroupBuilder id(Long id) { this.id = id; return this; }
        public GroupBuilder name(String name) { this.name = name; return this; }
        public GroupBuilder description(String description) { this.description = description; return this; }
        public GroupBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public GroupBuilder members(List<User> members) { this.members = members; return this; }
        public GroupBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Group build() {
            return new Group(id, name, description, createdBy, members, createdAt);
        }
    }
}
