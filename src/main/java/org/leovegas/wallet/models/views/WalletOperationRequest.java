package org.leovegas.wallet.models.views;

import java.math.BigInteger;

import static org.leovegas.wallet.utils.Preconditions.require;
import static org.leovegas.wallet.utils.Preconditions.requireNotNull;

public record WalletOperationRequest(
        int transactionId,
        BigInteger amount
) {
    public WalletOperationRequest {
        requireNotNull(transactionId, () -> "walletOperationRequest.transactionId can't be NULL");
        requireNotNull(amount, () -> "walletOperationRequest.amount can't be NULL");

        require(amount.signum() != -1, () -> "walletOperationRequest.amount should be more then Zero");
    }
}
