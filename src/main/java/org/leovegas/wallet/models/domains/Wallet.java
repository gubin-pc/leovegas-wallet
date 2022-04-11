package org.leovegas.wallet.models.domains;

import java.math.BigInteger;
import java.util.UUID;
import java.util.function.Function;

import static org.leovegas.wallet.utils.Preconditions.require;
import static org.leovegas.wallet.utils.Preconditions.requireNotNull;

public record Wallet(
        UUID id,
        int playerId,
        BigInteger debitBalance,
        BigInteger creditBalance,
        long version
) {

    public Wallet {
        requireNotNull(id, () -> "Parameter 'walletId' can't be NULL");
        requireNotNull(debitBalance, () -> "Parameter 'operation' can't be NULL");
        requireNotNull(creditBalance, () -> "Parameter 'status' can't be NULL");

        require(debitBalance.signum() != -1, () -> "Parameter 'debitBalance' should be more then Zero");
        require(creditBalance.signum() != -1, () -> "Parameter 'creditBalance' should be more then Zero");
    }

    public BigInteger balance() {
        return creditBalance.subtract(debitBalance);
    }

    public Wallet(UUID id, int playerId) {
        this(id, playerId, BigInteger.ZERO, BigInteger.ZERO, 0);
    }

    public <T> T mapTo(Function<Wallet, T> mappingFunc) {
        return mappingFunc.apply(this);
    }
}
