package org.leovegas.wallet.services;

import org.leovegas.wallet.controllers.SimpleControllerAdvice;
import org.leovegas.wallet.models.domains.Wallet;
import org.leovegas.wallet.repositories.WalletDao;
import org.leovegas.wallet.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class WalletOperationService {

    private final WalletDao walletDao;

    public WalletOperationService(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    public Wallet getOrCreateWallet(final int playerId) {
        return walletDao.findByPlayerId(playerId).orElseGet(() ->
                walletDao.insert(new Wallet(IdGenerator.generateWalletId(), playerId))
        );
    }

    public void creditBalance(final int playerId, final BigInteger amount) {
        Wallet wallet = getOrCreateWallet(playerId);
        walletDao.updateCreditBalance(wallet, amount);
    }

    public void debitBalance(final int playerId, final BigInteger amount) {
        // Optimistic locking algorithm
        Wallet wallet;
        do {
            wallet = getOrCreateWallet(playerId);
            if (wallet.balance().subtract(amount).signum() == -1) {
                throw new IllegalArgumentException("balance can't be less then Zero");
            }
        } while (!walletDao.updateDebitBalance(wallet, amount));
    }

}
