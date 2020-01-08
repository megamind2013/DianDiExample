package net.itdiandi.java.hyperloglog.streamlib;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HyperLogLogPlusIsLessThan {
    private long controlValue;
    private boolean orEqualTo;

    public HyperLogLogPlusIsLessThan() {
        // Required for serialisation
    }

    public HyperLogLogPlusIsLessThan(final long controlValue) {
        this(controlValue, false);
    }

    public HyperLogLogPlusIsLessThan(final long controlValue, final boolean orEqualTo) {
        this.controlValue = controlValue;
        this.orEqualTo = orEqualTo;
    }

    @JsonProperty("value")
    public long getControlValue() {
        return controlValue;
    }

    public void setControlValue(final long controlValue) {
        this.controlValue = controlValue;
    }

    public boolean getOrEqualTo() {
        return orEqualTo;
    }

    public void setOrEqualTo(final boolean orEqualTo) {
        this.orEqualTo = orEqualTo;
    }

    public boolean test(final HyperLogLogPlus input) {
        if (null == input) {
            return false;
        }
        final long cardinality = input.cardinality();
        if (orEqualTo) {
            if (cardinality <= controlValue) {
                return true;
            }
        } else {
            if (cardinality < controlValue) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (null == obj || getClass() != obj.getClass()) {
            return false;
        }

        final HyperLogLogPlusIsLessThan that = (HyperLogLogPlusIsLessThan) obj;

        return new EqualsBuilder()
                .append(controlValue, that.controlValue)
                .append(orEqualTo, that.orEqualTo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(79, 23)
                .append(controlValue)
                .append(orEqualTo)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "controlValue" + controlValue +",orEqualTo" + orEqualTo;
    }
}