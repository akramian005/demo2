package com.example.demo2.shop.service;

import com.example.demo2.shop.dto.cart.AddToCartRequest;
import com.example.demo2.shop.dto.cart.CartItemResponse;
import com.example.demo2.shop.entity.CartItem;
import com.example.demo2.shop.entity.Product;
import com.example.demo2.shop.repository.CartItemRepository;
import com.example.demo2.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartItemResponse addToCart(Long userId, AddToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найден"));

        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, product.getId());

        CartItem item;
        if (existing.isPresent()) {
            item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            item = new CartItem();
            item.setUserId(userId);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
        }

        if (item.getQuantity() > product.getStock()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недостаточно товара на складе");
        }

        CartItem saved = cartItemRepository.save(item);
        return toResponse(saved);
    }

    public List<CartItemResponse> getUserCart(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long cartItemId, Integer newQuantity) {
        CartItem item = getOwnedCartItem(userId, cartItemId);

        if (newQuantity > item.getProduct().getStock()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недостаточно товара на складе");
        }

        item.setQuantity(newQuantity);
        return toResponse(cartItemRepository.save(item));
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem item = getOwnedCartItem(userId, cartItemId);
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartItem getOwnedCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Позиция не найдена"));

        if (!item.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещён");
        }

        return item;
    }

    private CartItemResponse toResponse(CartItem item) {
        Product product = item.getProduct();
        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}