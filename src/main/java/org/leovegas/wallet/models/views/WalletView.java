package org.leovegas.wallet.models.views;

import java.math.BigInteger;

public record WalletView(
        int playerId,
        BigInteger balance
) {
}
