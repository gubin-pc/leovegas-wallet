package org.leovegas.wallet.models.views;

import java.math.BigInteger;

public record WalletOperationRequest(
        String id,
        BigInteger sum
) {
}
