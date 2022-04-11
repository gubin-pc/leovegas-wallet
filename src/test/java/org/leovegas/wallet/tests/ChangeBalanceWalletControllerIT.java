package org.leovegas.wallet.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.AbstractIntegrationTest;
import org.leovegas.wallet.models.Operation;
import org.leovegas.wallet.models.domains.Transaction;
import org.leovegas.wallet.models.domains.Wallet;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.models.views.WalletOperationRequest;
import org.leovegas.wallet.models.views.WalletView;
import org.leovegas.wallet.repositories.TransactionDao;
import org.leovegas.wallet.repositories.WalletDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.leovegas.wallet.TestUtils.generatePlayerId;
import static org.leovegas.wallet.utils.IdGenerator.generateWalletId;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class ChangeBalanceWalletControllerIT extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private ObjectMapper mapper;


    @Nested
    @DisplayName("debit player balance context")
    class DebitPlayerBalanceContext {

        @Test
        @DisplayName("should decrease balance for user")
        void shouldDecreaseBalance() {
            //given
            final int playerId = generatePlayerId();
            final int transactionId = generatePlayerId();
            final BigInteger amount = new BigInteger("5");
            Wallet wallet = new Wallet(
                    generateWalletId(),
                    playerId,
                    new BigInteger("0"),
                    new BigInteger("50"),
                    0
            );
            TransactionView expectedTransactionView = new TransactionView(
                    transactionId,
                    amount,
                    Operation.DEBIT,
                    Instant.now()
            );


            //then
            walletDao.insert(wallet);

            //when
            TransactionView actualTransactionView = webTestClient.post()
                    .uri("/players/{playerId}/wallet/debit", playerId)
                    .bodyValue(new WalletOperationRequest(transactionId, amount))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TransactionView.class)
                    .returnResult()
                    .getResponseBody();

            assertEquals(expectedTransactionView.id(), actualTransactionView.id());
            assertEquals(expectedTransactionView.amount(), actualTransactionView.amount());
            assertEquals(expectedTransactionView.operation(), actualTransactionView.operation());

            webTestClient.get()
                    .uri("/players/{playerId}/wallet", playerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(WalletView.class)
                    .isEqualTo(new WalletView(
                            playerId,
                            wallet.balance().subtract(amount)
                    ));

        }

        @Test
        @DisplayName("should throw if balance can be below Zero")
        void shouldThrowIfBalanceIsNegative() {
            //given
            final int playerId = generatePlayerId();
            final int transactionId = generatePlayerId();
            final BigInteger amount = new BigInteger("5");
            final Wallet wallet = new Wallet(
                    generateWalletId(),
                    playerId
            );

            //then
            walletDao.insert(wallet);

            //when
            webTestClient.post()
                    .uri("/players/{playerId}/wallet/debit", playerId)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(new WalletOperationRequest(transactionId, amount))
                    .exchange()
                    .expectStatus().isBadRequest();

            webTestClient.get()
                    .uri("/players/{playerId}/wallet", playerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(WalletView.class)
                    .isEqualTo(wallet.mapTo(w -> new WalletView(playerId, w.balance())));

        }

        @Test
        @DisplayName("should throw if amount is negative")
        void shouldThrowIfAmountIsNegative() throws JsonProcessingException {
            //given
            final int playerId = generatePlayerId();
            final int transactionId = generatePlayerId();
            final BigInteger amount = new BigInteger("-5");
            final Wallet wallet = new Wallet(
                    generateWalletId(),
                    playerId
            );

            final String walletOperationRawRequest = mapper.writeValueAsString(Map.of(
                    "transactionId", transactionId,
                    "amount", amount
            ));
            walletDao.insert(wallet);

            //when
            //then
            webTestClient.post()
                    .uri("/players/{playerId}/wallet/debit", playerId)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(walletOperationRawRequest)
                    .exchange()
                    .expectStatus().isBadRequest();

            webTestClient.get()
                    .uri("/players/{playerId}/wallet", playerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(WalletView.class)
                    .isEqualTo(wallet.mapTo(w -> new WalletView(playerId, w.balance())));

        }

    }

    @Nested
    @DisplayName("credit player balance context")
    class CreditPlayerBalanceContext {

        @Test
        @DisplayName("should increase balance for player")
        void shouldIncreaseBalance() {
            //given
            final int playerId = generatePlayerId();
            final int transactionId = generatePlayerId();
            final BigInteger amount = new BigInteger("5");
            Wallet wallet = new Wallet(
                    generateWalletId(),
                    playerId
            );
            TransactionView expectedTransactionView = new TransactionView(
                    transactionId,
                    amount,
                    Operation.CREDIT,
                    Instant.now()
            );
            walletDao.insert(wallet);


            //when
            //then
            TransactionView actualTransactionView = webTestClient.post()
                    .uri("/players/{playerId}/wallet/credit", playerId)
                    .bodyValue(new WalletOperationRequest(transactionId, amount))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TransactionView.class)
                    .returnResult()
                    .getResponseBody();
            assertEquals(expectedTransactionView.id(), actualTransactionView.id());
            assertEquals(expectedTransactionView.amount(), actualTransactionView.amount());
            assertEquals(expectedTransactionView.operation(), actualTransactionView.operation());

            webTestClient.get()
                    .uri("/players/{playerId}/wallet", playerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(WalletView.class)
                    .isEqualTo(new WalletView(
                            playerId,
                            wallet.balance().add(amount)
                    ));

        }

        @Test
        @DisplayName("should throw if amount is negative")
        void shouldThrowIfAmountIsNegative() throws JsonProcessingException {
            //given
            final int playerId = generatePlayerId();
            final int transactionId = generatePlayerId();
            final BigInteger amount = new BigInteger("-5");
            final Wallet wallet = new Wallet(
                    generateWalletId(),
                    playerId
            );
            final TransactionView expectedTransactionView = new TransactionView(
                    transactionId,
                    amount,
                    Operation.CREDIT,
                    Instant.now()
            );
            final String walletOperationRawRequest = mapper.writeValueAsString(Map.of(
                    "transactionId", transactionId,
                    "amount", amount
            ));
            walletDao.insert(wallet);

            //when
            //then
            webTestClient.post()
                    .uri("/players/{playerId}/wallet/debit", playerId)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(walletOperationRawRequest)
                    .exchange()
                    .expectStatus().isBadRequest();

            webTestClient.get()
                    .uri("/players/{playerId}/wallet", playerId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(WalletView.class)
                    .isEqualTo(wallet.mapTo(w -> new WalletView(playerId, w.balance())));

        }

    }

    @Test
    @DisplayName("should throw if transaction exist")
    void shouldThrowIfTransactionExist() {
        //given
        final int playerId = generatePlayerId();
        final int transactionId = generatePlayerId();
        final UUID walletId = generateWalletId();
        final BigInteger amount = new BigInteger("5");
        final Wallet wallet = new Wallet(walletId, playerId);


        walletDao.insert(wallet);
        transactionDao.insert(new Transaction(transactionId,
                walletId,
                new BigInteger("50"),
                Operation.CREDIT
        ));

        //when
        //then
        webTestClient.post()
                .uri("/players/{playerId}/wallet/credit", playerId)
                .bodyValue(new WalletOperationRequest(transactionId, amount))
                .exchange()
                .expectStatus().isBadRequest();

        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(wallet.mapTo(w -> new WalletView(playerId, w.balance())));

    }


}
