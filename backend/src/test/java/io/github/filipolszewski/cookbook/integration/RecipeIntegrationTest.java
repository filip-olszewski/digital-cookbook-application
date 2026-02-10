package io.github.filipolszewski.cookbook.integration;

import io.github.filipolszewski.cookbook.model.embeddable.Name;
import io.github.filipolszewski.cookbook.model.entity.Category;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.model.enumeration.Role;
import io.github.filipolszewski.cookbook.repository.CategoryRepository;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDate;

class RecipeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        testUser = userRepository.save(new User(
            "email@test.com",
            "testuser123",
            new Name("first", null, "last"),
            "pass",
            Role.USER,
            null,
            null
        ));

        testCategory = categoryRepository.save(new Category(
                "Dinner",
                "dinner",
                null,
                null,
                null
        ));
    }

    @Test
    void shouldReturnListOfRecipes() {
        Recipe recipe = createTestRecipe("Tomato Pasta", "tomato-pasta");


    }

    private Recipe createTestRecipe(String name, String slug) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setSlug(slug);
        recipe.setDescription("A delicious test recipe description that is long enough.");
        recipe.setPrepTime(20);
        recipe.setServings(4);
        recipe.setPublicationDate(LocalDate.now());
        recipe.setAverageRating(0.0);
        recipe.setReviewCount(0);
        recipe.setFavouriteCount(0);
        recipe.setAuthor(testUser);
        recipe.setCategory(testCategory);
        return recipeRepository.save(recipe);
    }

}