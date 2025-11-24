package io.github.filipolszewski.cookbook.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recipe_ingredients", uniqueConstraints = {
        @UniqueConstraint(name = "recipe_ingredients",
                columnNames = {"recipe_id", "ingredient_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient extends BaseEntity {
    @NotNull
    @Column(nullable = false)
    private Integer amount;

    @NotBlank
    @Column(nullable = false)
    private String unit;

    // RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
}
