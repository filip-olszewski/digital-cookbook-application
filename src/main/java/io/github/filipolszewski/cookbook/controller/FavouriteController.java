package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.service.FavouriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1)
@Tag(name = "Favourites", description = "Endpoints for managing user favourite recipes")
public class FavouriteController {

    private final FavouriteService favouriteService;

    @Operation(
        summary = "Add recipe to favourites",
        description = "Adds a recipe to the authenticated user's favourites. Requires a valid JWT.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/recipes/{id}/favourite")
    public ResponseEntity<Void> addToFavourites(@PathVariable Long id) {
        favouriteService.addToFavourites(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Remove recipe from favourites",
        description = "Removes a recipe from the authenticated user's favourites. Requires a valid JWT.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/recipes/{id}/favourite")
    public ResponseEntity<Void> removeFromFavourites(@PathVariable Long id) {
        favouriteService.removeFromFavourites(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get user's favourite recipes",
        description = "Retrieves a paged list of the authenticated user's favourite recipes. Requires a valid JWT.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/users/me/favourites")
    public ResponseEntity<Page<RecipeSummaryResponse>> getUserFavourites(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(favouriteService.getUserFavourites(pageable));
    }
}