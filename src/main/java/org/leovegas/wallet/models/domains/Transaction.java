package org.leovegas.wallet.models.domains;

import org.leovegas.wallet.models.Operation;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

import static org.leovegas.wallet.utils.Preconditions.require;
import static org.leovegas.wallet.utils.Preconditions.requireNotNull;


public record Transaction(
        int id,
        UUID walletId,
        BigInteger amount,
        Operation operation,
        Instant createdAt
) {

    public Transaction {
        requireNotNull(walletId, () -> "Parameter 'walletId' can't be NULL");
        requireNotNull(amount, () -> "Parameter 'amount' can't be NULL");
        requireNotNull(operation, () -> "Parameter 'operation' can't be NULL");
        requireNotNull(createdAt, () -> "Parameter 'createdAt' can't be NULL");

        require(amount.signum() != -1, () -> "Parameter 'amount' should be more then Zero");
    }

    public Transaction(int id, UUID walletId, BigInteger sum, Operation operation) {
        this(id, walletId, sum, operation, Instant.now());
    }

    public <T> T mapTo(Function<Transaction, T> mappingFunc) {
        return mappingFunc.apply(this);
    }

}
