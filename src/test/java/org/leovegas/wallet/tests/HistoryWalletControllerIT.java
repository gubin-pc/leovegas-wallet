package org.leovegas.wallet.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.AbstractIntegrationTest;
import org.leovegas.wallet.controllers.SimpleControllerAdvice;
import org.leovegas.wallet.models.Operation;
import org.leovegas.wallet.models.domains.Transaction;
import org.leovegas.wallet.models.domains.Wallet;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.repositories.TransactionDao;
import org.leovegas.wallet.repositories.WalletDao;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.leovegas.wallet.TestUtils.generatePlayerId;
import static org.leovegas.wallet.TestUtils.generateTransactionId;
import static org.leovegas.wallet.utils.IdGenerator.generateWalletId;

public class HistoryWalletControllerIT extends AbstractIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(SimpleControllerAdvice.class);

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private TransactionDao transactionDao;

    @Test
    @DisplayName("should return balance for existed user")
    void shouldReturnBalanceExistedUser() {
        //given
        final int playerId = generatePlayerId();
        UUID walletId = walletDao.insert(new Wallet(
                generateWalletId(),
                playerId
        )).id();

        List<Transaction> transactions = List.of(
                new Transaction(
                        generateTransactionId(),
                        walletId,
                        BigInteger.ONE,
                        Operation.CREDIT
                ),
                new Transaction(
                        generateTransactionId(),
                        walletId,
                        BigInteger.ONE,
                        Operation.DEBIT
                ),
                new Transaction(
                        generateTransactionId(),
                        walletId,
                        BigInteger.ONE,
                        Operation.CREDIT
                ),
                new Transaction(
                        generateTransactionId(),
                        walletId,
                        BigInteger.ONE,
                        Operation.DEBIT
                )
        );

        transactions.forEach(transaction -> transactionDao.insert(transaction));


        //when
        List<TransactionView> actualList = webTestClient.get()
                .uri("/players/{playerId}/wallet/history", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<TransactionView>>() { })
                .returnResult()
                .getResponseBody();

        //then
        List<TransactionView> expectedList = transactions.stream().map(transaction ->
                new TransactionView(
                        transaction.id(),
                        transaction.amount(),
                        transaction.operation(),
                        transaction.createdAt()
                )
        ).toList();

        assertEquals(expectedList.size(), actualList.size());
        for (TransactionView expected : expectedList) {
            TransactionView actual = findById(actualList, expected.id()).orElseThrow(() -> new AssertionFailedError(null, expected, null));
            assertEquals(expected.id(), actual.id());
            assertEquals(expected.amount(), actual.amount());
            assertEquals(expected.operation(), actual.operation());
        }
    }

    private Optional<TransactionView> findById(List<TransactionView> actualList, int id) {
        return actualList.stream().filter(tv -> tv.id() == id).findFirst();
    }

}
