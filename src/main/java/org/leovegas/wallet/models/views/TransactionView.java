package org.leovegas.wallet.models.views;

import org.leovegas.wallet.models.Operation;
import org.leovegas.wallet.models.Status;

import java.math.BigInteger;
import java.time.Instant;

public record TransactionView(
        String id,
        BigInteger sum,
        Operation operation,
        Status status,
        Instant created_at
) {
}
