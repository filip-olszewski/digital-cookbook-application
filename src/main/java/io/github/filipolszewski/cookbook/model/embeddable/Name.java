package io.github.filipolszewski.cookbook.model.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record Name(
    @NotBlank @Column(nullable = false) String firstName,
    String middleName,
    @NotBlank @Column(nullable = false) String lastName
) {

    public Name(String firstName, String lastName) {
        this(firstName, null, lastName);
    }

    public String getFullName() {
        StringBuilder builder = new StringBuilder(firstName);
        if(middleName != null && !middleName.isBlank()) {
            builder.append(" ").append(middleName);
        }
        builder.append(" ").append(lastName);
        return builder.toString();
    }
}
