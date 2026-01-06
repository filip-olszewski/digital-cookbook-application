package io.github.filipolszewski.cookbook.model.entity;

import io.github.filipolszewski.cookbook.annotation.DatabaseUnique;
import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE ingredients SET deleted_at = CURRENT_TIMESTAMP where id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class Ingredient extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    @DatabaseUnique
    private String name;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IngredientType type;
}
