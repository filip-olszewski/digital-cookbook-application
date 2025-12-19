package io.github.filipolszewski.cookbook.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields annotated with this annotation have a Unique Constraint defined at a database level.
 * This approach lets the application implement soft delete operations without any clashing with unique fields.
 * Even though <code>@Column(unique = true)</code> annotation is missing, this field is to be unique.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface DatabaseUnique {
}
