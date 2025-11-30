package io.github.filipolszewski.cookbook.controller;

import com.sun.net.httpserver.HttpsServer;
import io.github.filipolszewski.cookbook.constants.ApiConstants;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<IngredientSummaryResponse> addIngredient(@RequestBody @Valid IngredientCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.addIngredient(request));
    }

}
