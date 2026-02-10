package com.logistics.company.service;

import com.logistics.company.data.DeliveryType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {
    private static final BigDecimal BASE_PRICE = BigDecimal.valueOf(5.00);
    private static final BigDecimal KG_PRICE = BigDecimal.valueOf(2.50);
    private static final BigDecimal ADDRESS_FEE = BigDecimal.valueOf(10.00);

    public BigDecimal calculatePrice(double weight, DeliveryType deliveryType) {
        BigDecimal total = BASE_PRICE.add(BigDecimal.valueOf(weight).multiply(KG_PRICE));

        if (deliveryType == DeliveryType.TO_ADDRESS) {
            total = total.add(ADDRESS_FEE);
        }
        return total;
    }
}