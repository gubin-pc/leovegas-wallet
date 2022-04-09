package org.leovegas.wallet.services;

import org.jetbrains.annotations.Nullable;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.repositories.TransactionDao;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HistoryService {

    private final TransactionDao transactionDao;

    public HistoryService(final TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public List<TransactionView> getHistory(
            final int playerId,
            @Nullable final LocalDate from,
            @Nullable final LocalDate to) {
        return transactionDao.findAll(playerId, from, to).stream().map(transaction ->
                new TransactionView(
                        transaction.id(),
                        transaction.amount(),
                        transaction.operation(),
                        transaction.createdAt()
                )
        ).collect(Collectors.toList());
    }

}
