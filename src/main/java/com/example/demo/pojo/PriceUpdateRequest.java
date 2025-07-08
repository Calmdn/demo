package com.example.demo.pojo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PriceUpdateRequest {
    private Integer artworkId;
    private BigDecimal newPrice;
    private String changeReason;
}