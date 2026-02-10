package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;

    @PostMapping("/recipes/{id}/favourite")
    public ResponseEntity<Void> addToFavourites(@PathVariable Long id) {
        favouriteService.addToFavourites(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/recipes/{id}/favourite")
    public ResponseEntity<Void> removeFromFavourites(@PathVariable Long id) {
        favouriteService.removeFromFavourites(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/me/favourites")
    public ResponseEntity<Page<RecipeSummaryResponse>> getUserFavourites(Pageable pageable) {
        return ResponseEntity.ok(favouriteService.getUserFavourites(pageable));
    }
}
