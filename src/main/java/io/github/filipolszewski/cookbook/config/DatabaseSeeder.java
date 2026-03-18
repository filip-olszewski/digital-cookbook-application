package io.github.filipolszewski.cookbook.config;

import com.github.slugify.Slugify;
import io.github.filipolszewski.cookbook.model.embeddable.Name;
import io.github.filipolszewski.cookbook.model.entity.*;
import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;
import io.github.filipolszewski.cookbook.model.enumeration.Role;
import io.github.filipolszewski.cookbook.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Profile("!prod")
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final Slugify slugify;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains application data. Skipping Datafaker seed.");
            return;
        }

        log.info("Starting database seeding with mock data.");
        Faker faker = new Faker();
        Random random = new Random();

        List<User> users = seedUsers(faker);
        List<Ingredient> ingredients = seedIngredients(faker);
        List<Category> categories = categoryRepository.findAll();
        List<Tag> tags = tagRepository.findAll();

        if (!categories.isEmpty() && !tags.isEmpty()) {
            seedRecipes(faker, random, users, categories, tags, ingredients);
        } else {
            log.warn("Categories or Tags missing! Did Flyway run properly?");
        }

        log.info("Mock database seeding completed successfully!");
    }

    private List<User> seedUsers(Faker faker) {
        List<User> users = new ArrayList<>();

        User admin = new User();
        admin.setEmail("admin@admin.com");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(Role.ADMIN);
        admin.setBio("Digital Cookbook Admin");
        admin.setName(new Name("John", "Doe"));
        users.add(userRepository.save(admin));

        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setEmail(faker.internet().emailAddress());
            user.setUsername(faker.internet().username());
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole(Role.USER);
            user.setBio(faker.lorem().sentence());
            user.setAvatarUrl(faker.internet().image());
            user.setName(new Name(faker.name().firstName(), faker.name().lastName()));
            users.add(userRepository.save(user));
        }
        return users;
    }

    private List<Ingredient> seedIngredients(Faker faker) {
        List<Ingredient> ingredients = new ArrayList<>();
        Set<String> uniqueIngredientNames = generateUniqueNames(faker, 40, false);

        Random random = new Random();
        IngredientType[] types = IngredientType.values();

        for (String uniqueName : uniqueIngredientNames) {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(uniqueName);
            ingredient.setType(types[random.nextInt(types.length)]);
            ingredients.add(ingredientRepository.save(ingredient));
        }

        return ingredients;
    }

    private void seedRecipes(Faker faker, Random random, List<User> users,
                             List<Category> categories, List<Tag> tags, List<Ingredient> ingredients) {

        Set<String> uniqueDishNames = generateUniqueNames(faker, 25, true);

        for (String dishName : uniqueDishNames) {
            Recipe recipe = buildBaseRecipe(dishName, faker, random, users, categories, tags);
            recipe = recipeRepository.save(recipe);

            addIngredientsToRecipe(recipe, faker, random, ingredients);
            recipe = recipeRepository.save(recipe);

            addReviewsAndSyncRatings(recipe, faker, random, users);
        }
    }

    private Recipe buildBaseRecipe(String dishName, Faker faker, Random random,
                                   List<User> users, List<Category> categories, List<Tag> tags) {
        Recipe recipe = new Recipe();
        recipe.setName(dishName);
        recipe.setSlug(slugify.slugify(dishName));
        recipe.setDescription(faker.lorem().paragraph(2));
        recipe.setPrepTime(random.nextInt(10, 45));
        recipe.setServings(random.nextInt(1, 8));
        recipe.setPublicationDate(LocalDate.now());
        recipe.setFavouriteCount(random.nextInt(0, 200));

        recipe.setAuthor(users.get(random.nextInt(users.size())));
        recipe.setCategory(categories.get(random.nextInt(categories.size())));

        int numTags = random.nextInt(1, 4);
        for (int j = 0; j < numTags; j++) {
            recipe.getTags().add(tags.get(random.nextInt(tags.size())));
        }
        return recipe;
    }

    private void addIngredientsToRecipe(Recipe recipe, Faker faker, Random random, List<Ingredient> ingredients) {
        int numIngredients = random.nextInt(3, 9);
        List<Ingredient> availableIngredients = new ArrayList<>(ingredients);
        Collections.shuffle(availableIngredients, random);

        for (int j = 0; j < numIngredients; j++) {
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(availableIngredients.get(j));
            recipeIngredient.setAmount(random.nextDouble(100, 1000));
            recipeIngredient.setUnit(faker.food().measurement());

            recipe.addIngredient(recipeIngredient);
        }
    }

    private void addReviewsAndSyncRatings(Recipe recipe, Faker faker, Random random, List<User> users) {
        int numReviews = Math.min(random.nextInt(0, 8), users.size());

        if (numReviews == 0) {
            recipe.setAverageRating(0.0);
            recipe.setReviewCount(0);
            recipeRepository.save(recipe);
            return;
        }

        double totalRating = 0;
        List<User> availableReviewers = new ArrayList<>(users);
        Collections.shuffle(availableReviewers, random);

        for (int j = 0; j < numReviews; j++) {
            Review review = new Review();
            review.setRecipe(recipe);
            review.setUser(availableReviewers.get(j));

            int rating = random.nextInt(1, 6);
            review.setRating(rating);
            review.setComment(faker.restaurant().review());

            reviewRepository.save(review);
            totalRating += rating;
        }

        double trueAverage = totalRating / numReviews;
        recipe.setAverageRating(Math.round(trueAverage * 10.0) / 10.0);
        recipe.setReviewCount(numReviews);

        recipeRepository.save(recipe);
    }

    private Set<String> generateUniqueNames(Faker faker, int count, boolean isDish) {
        Set<String> uniqueNames = new HashSet<>();
        while (uniqueNames.size() < count) {
            uniqueNames.add(isDish ? faker.food().dish() : faker.food().ingredient());
        }
        return uniqueNames;
    }
}