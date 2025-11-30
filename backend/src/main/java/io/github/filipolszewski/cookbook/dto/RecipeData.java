package io.github.filipolszewski.cookbook.dto;

import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.Step;
import io.github.filipolszewski.cookbook.model.entity.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecipeData {

    public record RecipeCreateRequest(
        @NotBlank   String name,
        @Size(max = Recipe.MAX_DESCRIPTION_LENGTH)
                    String description,
        @NotNull    Integer prepTime,
        @NotNull    Integer servings,
                    String imgUrl,
        @NotNull    Integer authorId,
        @NotNull    Integer categoryId,
                    Set<Tag> tagIds
        // List<IngredientDTO>
        // List<StepDTO>
    ) {}

}
