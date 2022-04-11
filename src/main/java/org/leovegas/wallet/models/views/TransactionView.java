package org.leovegas.wallet.models.views;

import org.leovegas.wallet.models.Operation;

import java.math.BigInteger;
import java.time.Instant;

public record TransactionView(
        int id,
        BigInteger amount,
        Operation operation,
        Instant createdAt
) { }
