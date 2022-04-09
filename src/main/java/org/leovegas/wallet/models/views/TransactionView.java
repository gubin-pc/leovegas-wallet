package org.leovegas.wallet.models.views;

import org.leovegas.wallet.models.Operation;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Objects;

public record TransactionView(
        int id,
        BigInteger amount,
        Operation operation,
        Instant createdAt
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionView that = (TransactionView) o;
        return id == that.id && amount.equals(that.amount) && operation == that.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, operation);
    }
}
