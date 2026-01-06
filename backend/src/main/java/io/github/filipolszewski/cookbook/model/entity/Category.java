package io.github.filipolszewski.cookbook.model.entity;

import io.github.filipolszewski.cookbook.annotation.DatabaseUnique;
import io.github.filipolszewski.cookbook.exception.ResourceConflictException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE categories SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class Category extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    @DatabaseUnique
    private String slug;

    private String imgUrl;

    // RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToMany(
            mappedBy = "parentCategory",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Category> subCategories = new ArrayList<>();

    // BUSINESS METHODS
    public void moveTo(Category parent) {
        if (parentCategory != null && parentCategory != parent) {
            parentCategory.getSubCategories().remove(this);
        }

        if(parent == null) {
            parentCategory = null;
            return;
        }

        if(getId().equals(parent.getId())) {
            throw new ResourceConflictException("Category cannot be its own parent.");
        }

        if (isDescendantOf(parent)) {
            throw new ResourceConflictException("Cannot move a category into its own sub-category.");
        }

        parentCategory = parent;
        parent.getSubCategories().add(this);
    }

    public boolean isDescendantOf(Category potentialAncestor) {
        Category current = this.getParentCategory();
        while (current != null) {
            if (current.getId().equals(potentialAncestor.getId())) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }
}
