package org.leovegas.wallet.services;

import org.leovegas.wallet.controllers.SimpleControllerAdvice;
import org.leovegas.wallet.models.Operation;
import org.leovegas.wallet.models.domains.Transaction;
import org.leovegas.wallet.models.domains.Wallet;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.repositories.TransactionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Component
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(SimpleControllerAdvice.class);

    private final WalletOperationService walletOperationService;
    private final TransactionDao transactionDao;

    public TransactionService(final WalletOperationService walletOperationService,
                              final TransactionDao transactionDao) {
        this.walletOperationService = walletOperationService;
        this.transactionDao = transactionDao;
    }


    @Transactional
    public TransactionView changePlayerBalance(
            final int playerId,
            final int transactionId,
            final BigInteger amount,
            final Operation operation
    ) {
        Wallet wallet = walletOperationService.getOrCreateWallet(playerId);

        try {

            Transaction transaction = transactionDao.insert(new Transaction(
                    transactionId,
                    wallet.id(),
                    amount,
                    operation
            ));
            switch (operation) {
                case CREDIT -> walletOperationService.creditBalance(playerId, amount);
                case DEBIT -> walletOperationService.debitBalance(playerId, amount);
            }
            return transaction.mapTo(t -> new TransactionView(
                    t.id(),
                    t.amount(),
                    t.operation(),
                    t.createdAt()
            ));

        } catch (DuplicateKeyException e) {
            log.warn(e.getMessage(), e);
            throw new IllegalArgumentException("Transaction '" + transactionId + "' have already Settled");
        }

    }


}
