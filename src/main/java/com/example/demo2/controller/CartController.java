package com.example.demo2.controller;

import com.example.demo2.dto.cart.AddToCartRequest;
import com.example.demo2.dto.cart.CartItemResponse;
import com.example.demo2.dto.cart.UpdateCartItemRequest;
import com.example.demo2.entity.User;
import com.example.demo2.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getUserCart(user.getId()));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddToCartRequest request
    ) {
        return ResponseEntity.ok(cartService.addToCart(user, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(cartService.updateQuantity(user, id, request.getQuantity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        cartService.removeFromCart(user, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
