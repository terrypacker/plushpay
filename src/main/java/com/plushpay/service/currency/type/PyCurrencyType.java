package com.plushpay.service.currency.type;

// Generated Apr 24, 2010 1:54:05 PM by Hibernate Tools 3.2.5.Beta

import com.plushpay.repository.IdEntity;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * PyCurrencyType
 * <p>
 * A quote of any given currency with a rate of 1/10000 to base currency
 */
public class PyCurrencyType implements IdEntity<Long> {

    private Long id;
    private CurrencyCodeEnum code;
    private long rateToBase;
    private ZonedDateTime date;
    private boolean base;

    public PyCurrencyType() {
    }

    public PyCurrencyType(CurrencyCodeEnum code, long rateToBase, ZonedDateTime date) {
        this.code = code;
        this.rateToBase = rateToBase;
        this.date = date;
    }

    public PyCurrencyType(CurrencyCodeEnum code, long rateToBase,
        String symbol, ZonedDateTime date, boolean base) {
        this.code = code;
        this.rateToBase = rateToBase;
        this.date = date;
        this.base = base;
    }

    public PyCurrencyType(PyCurrencyType type) {
        this.id = type.id;
        this.code = type.code;
        this.rateToBase = type.rateToBase;
        this.date = type.date;
        this.base = type.base;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyCodeEnum getCode() {
        return this.code;
    }

    public void setCode(CurrencyCodeEnum code) {
        this.code = code;
    }

    public long getRateToBase() {
        return this.rateToBase;
    }

    public void setRateToBase(long rateToBase) {
        this.rateToBase = rateToBase;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public boolean isBase() {
        return this.base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    /**
     * toString
     *
     * @return String
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName()).append("@").append(
            Integer.toHexString(hashCode())).append(" [\n");
        buffer.append("id").append("='").append(getId()).append("'\n");
        buffer.append("code").append("='").append(getCode()).append("'\n");
        buffer.append("rateToBase").append("='").append(getRateToBase())
            .append("'\n");
        buffer.append("date").append("='").append(getDate()).append("'\n");
        buffer.append("base").append("='").append(isBase()).append("'\n");
        buffer.append("]\n");

        return buffer.toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if ((other == null)) {
            return false;
        }
        if (!(other instanceof PyCurrencyType)) {
            return false;
        }
        PyCurrencyType castOther = (PyCurrencyType) other;

        return (this.getId() == castOther.getId())
            && ((this.getCode() == castOther.getCode()) || (this.getCode() != null
            && castOther.getCode() != null && this.getCode()
            .equals(castOther.getCode())))
            && (this.getRateToBase() == castOther.getRateToBase())
            && ((this.getDate() == castOther.getDate()) || (this.getDate() != null
            && castOther.getDate() != null && this.getDate()
            .equals(castOther.getDate())))
            && (this.isBase() == castOther.isBase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, rateToBase, date, base);
    }
}
