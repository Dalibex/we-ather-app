package es.uma.weather.heatmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HeatScoreCalculatorTest {

    private final HeatScoreCalculator calculator = new HeatScoreCalculator();

    @Test
    void scoreIsBoundedBetweenZeroAndOneHundred() {
        assertEquals(0.0, calculator.calculate(-10, 0, 100, 0), 0.1);
        assertTrue(calculator.calculate(60, 120, 0, 1200) <= 100.0);
    }

    @Test
    void hotterAndMoreExposedConditionsProduceHigherScore() {
        double mild = calculator.calculate(22, 35, 20, 100);
        double hot = calculator.calculate(39, 75, 3, 850);
        assertTrue(hot > mild);
    }

    @Test
    void assignsExpectedRiskBands() {
        assertEquals("LOW", calculator.level(10));
        assertEquals("MODERATE", calculator.level(30));
        assertEquals("HIGH", calculator.level(60));
        assertEquals("VERY_HIGH", calculator.level(90));
    }
}
