package io.github.filipolszewski.cookbook.model.entity;

import io.github.filipolszewski.cookbook.annotation.DatabaseUnique;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE recipes SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class Recipe extends BaseEntity {

    public static final int MAX_DESCRIPTION_LENGTH = 5000;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    @DatabaseUnique
    private String slug;

    @NotBlank
    @Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotNull
    @Column(nullable = false)
    private Integer prepTime;

    @NotNull
    @Column(nullable = false)
    private Integer servings;

    @NotNull
    @Column(nullable = false)
    private LocalDate publicationDate;

    private String imgUrl;

    /**
     * Cached average rating for performance optimization.
     * Denormalized to enable efficient sorting and filtering.
     * Updates are handled by {@link io.github.filipolszewski.cookbook.service.ReviewService}.
     */
    @NotNull
    @Column(nullable = false)
    private Double averageRating = 0.0;

    /**
     * Cached average rating for performance optimization.
     * Denormalized to enable efficient sorting and filtering.
     * Updates are handled by {@link io.github.filipolszewski.cookbook.service.ReviewService}.
     */
    @NotNull
    @Column(nullable = false)
    private Integer reviewCount = 0;

    // RELATIONS
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "recipe_tags",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true)
    private Set<Favourite> favourites = new HashSet<>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true)
    private Set<Step> steps = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
