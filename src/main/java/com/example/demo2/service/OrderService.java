package com.example.demo2.service;

import com.example.demo2.dto.order.*;
import com.example.demo2.entity.*;
import com.example.demo2.repository.CartItemRepository;
import com.example.demo2.repository.OrderRepository;
import com.example.demo2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(User user, CreateOrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Корзина пуста");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(toAddressEntity(request.getShippingAddress()));

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Недостаточно товара на складе: " + product.getName()
                );
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(cartItem.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            order.addItem(item);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.recalculateTotalPrice();
        Order saved = orderRepository.save(order);

        cartItemRepository.deleteByUserId(user.getId());

        return toResponse(saved);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещён");
        }

        return toResponse(order);
    }

    private Address toAddressEntity(AddressRequest request) {
        Address address = new Address();
        address.setCountry(request.getCountry());
        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setPostalCode(request.getPostalCode());
        return address;
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase()
                ))
                .toList();

        AddressRequest addressDto = new AddressRequest();
        addressDto.setCountry(order.getShippingAddress().getCountry());
        addressDto.setCity(order.getShippingAddress().getCity());
        addressDto.setStreet(order.getShippingAddress().getStreet());
        addressDto.setPostalCode(order.getShippingAddress().getPostalCode());

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                addressDto,
                items
        );
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден"));

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Нельзя изменить статус завершённого заказа: " + current
            );
        }

        boolean validTransition = switch (current) {
            case PENDING -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            default -> false;
        };

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Недопустимый переход статуса: " + current + " → " + next
            );
        }
    }
}
