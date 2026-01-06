package io.github.filipolszewski.cookbook.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE steps SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?") // <--- ADD THIS
@SQLRestriction("deleted_at IS NULL")
public class Step extends BaseEntity {

    public static final int MAX_INSTRUCTIONS_LENGTH = 5000;

    @NotNull
    @Column(nullable = false)
    private Integer stepOrder;

    @NotBlank
    @Column(nullable = false, length = MAX_INSTRUCTIONS_LENGTH)
    private String instructions;

    private String imgUrl;

    // RELATIONS
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
}
