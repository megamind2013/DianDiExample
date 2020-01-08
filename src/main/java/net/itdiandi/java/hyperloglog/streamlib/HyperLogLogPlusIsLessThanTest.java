package net.itdiandi.java.hyperloglog.streamlib;


import java.util.function.Predicate;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class HyperLogLogPlusIsLessThanTest {

    private static HyperLogLogPlus hyperLogLogPlusWithCardinality5;
    private static HyperLogLogPlus hyperLogLogPlusWithCardinality15;
    private static HyperLogLogPlus hyperLogLogPlusWithCardinality31;

    public static void main(String[] args) {
        hyperLogLogPlusWithCardinality5 = new HyperLogLogPlus(5, 5);
        for (int i = 1; i <= 5; i++) {
            hyperLogLogPlusWithCardinality5.offer(i);
        }
        assertEquals(5L, hyperLogLogPlusWithCardinality5.cardinality());

        hyperLogLogPlusWithCardinality15 = new HyperLogLogPlus(5, 5);
        for (int i = 1; i <= 18; i++) {
            hyperLogLogPlusWithCardinality15.offer(i);
        }
        assertEquals(15L, hyperLogLogPlusWithCardinality15.cardinality());

        hyperLogLogPlusWithCardinality31 = new HyperLogLogPlus(5, 5);
        for (int i = 1; i <= 32; i++) {
            hyperLogLogPlusWithCardinality31.offer(i);
        }
        assertEquals(31L, hyperLogLogPlusWithCardinality31.cardinality());
    }

    @Test
    public void shouldAcceptWhenLessThan() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter.test(hyperLogLogPlusWithCardinality5);
        // Then
        assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenEqualToAndEqualToIsFalse() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter.test(hyperLogLogPlusWithCardinality15);
        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldAcceptWhenEqualToAndEqualToIsTrue() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15, true);
        // When
        boolean accepted = filter.test(hyperLogLogPlusWithCardinality15);
        // Then
        assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenMoreThan() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter.test(hyperLogLogPlusWithCardinality31);
        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldRejectWhenInputIsNull() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter.test(null);
        // Then
        assertFalse(accepted);
    }
}