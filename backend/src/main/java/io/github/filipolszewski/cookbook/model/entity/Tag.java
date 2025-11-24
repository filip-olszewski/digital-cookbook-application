package io.github.filipolszewski.cookbook.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE tags SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class Tag extends BaseEntity {
    @NotBlank
    @Column(nullable = false)
    private String label;

    // RELATIONS
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Recipe> recipes = new HashSet<>();
}
