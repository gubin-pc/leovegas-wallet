package org.leovegas.wallet.models.views;

import java.math.BigInteger;

public record WalletView(
        String userId,
        BigInteger balance
) {
}
