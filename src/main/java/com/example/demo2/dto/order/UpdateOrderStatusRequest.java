package com.example.demo2.dto.order;


import com.example.demo2.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotNull(message = "Статус обязателен")
    private OrderStatus status;
}
