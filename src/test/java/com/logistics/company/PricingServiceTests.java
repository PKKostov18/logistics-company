package com.logistics.company;

import com.logistics.company.data.DeliveryType;
import com.logistics.company.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTests {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
    }

    @Test
    void calculatePrice_ShouldReturnBasePlusKgPrice_WhenToOffice() {
        BigDecimal result = pricingService.calculatePrice(2.0, DeliveryType.TO_OFFICE);

        assertEquals(0, BigDecimal.valueOf(10.00).compareTo(result));
    }

    @Test
    void calculatePrice_ShouldAddAddressFee_WhenToAddress() {
        BigDecimal result = pricingService.calculatePrice(2.0, DeliveryType.TO_ADDRESS);

        assertEquals(0, BigDecimal.valueOf(20.00).compareTo(result));
    }

    @Test
    void calculatePrice_ShouldReturnBasePrice_WhenWeightIsZero_Office() {
        BigDecimal result = pricingService.calculatePrice(0.0, DeliveryType.TO_OFFICE);

        assertEquals(0, BigDecimal.valueOf(5.00).compareTo(result));
    }

    @Test
    void calculatePrice_ShouldReturnBasePlusAddressFee_WhenWeightIsZero_Address() {
        BigDecimal result = pricingService.calculatePrice(0.0, DeliveryType.TO_ADDRESS);

        assertEquals(0, BigDecimal.valueOf(15.00).compareTo(result));
    }

    @Test
    void calculatePrice_ShouldWorkWithDecimalWeight() {
        BigDecimal result = pricingService.calculatePrice(1.5, DeliveryType.TO_OFFICE);

        assertEquals(0, BigDecimal.valueOf(8.75).compareTo(result));
    }

    @Test
    void calculatePrice_ShouldWorkWithLargeWeight() {
        BigDecimal result = pricingService.calculatePrice(10.0, DeliveryType.TO_ADDRESS);

        assertEquals(0, BigDecimal.valueOf(40.00).compareTo(result));
    }
}
