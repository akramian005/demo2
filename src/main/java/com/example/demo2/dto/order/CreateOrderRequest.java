package com.example.demo2.dto.order;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @Valid // ВАЖНО — заставляет провалидировать вложенный AddressRequest
    private AddressRequest shippingAddress;
}
