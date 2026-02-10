    package io.github.filipolszewski.cookbook.controller;

    import io.github.filipolszewski.cookbook.constant.ApiConstants;
    import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
    import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
    import io.github.filipolszewski.cookbook.dto.ingredient.IngredientUpdateRequest;
    import io.github.filipolszewski.cookbook.service.IngredientService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springdoc.core.annotations.ParameterObject;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping(ApiConstants.API_V1 + "/ingredients")
    public class IngredientController {

        private final IngredientService ingredientService;

        @GetMapping
        public ResponseEntity<Page<IngredientSummaryResponse>> getIngredients(
                @ParameterObject Pageable pageable,
                @RequestParam(required = false) String name
        ) {
            return ResponseEntity.ok(ingredientService.getIngredients(pageable, name));
        }

        @PostMapping
        public ResponseEntity<IngredientSummaryResponse> addIngredient(
                @RequestBody @Valid IngredientCreateRequest request) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.addIngredient(request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteIngredient(
                @PathVariable Long id
        ) {
            ingredientService.deleteIngredient(id);
            return ResponseEntity.noContent().build();

        }

        @PatchMapping("/{id}")
        public ResponseEntity<IngredientSummaryResponse> updateIngredient(
                @PathVariable Long id,
                @Valid @RequestBody IngredientUpdateRequest request
        ) {
            return ResponseEntity.ok(ingredientService.updateIngredient(id, request));
        }

    }
