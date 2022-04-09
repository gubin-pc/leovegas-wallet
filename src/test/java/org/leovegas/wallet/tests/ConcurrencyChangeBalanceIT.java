package org.leovegas.wallet.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.AbstractIntegrationTest;
import org.leovegas.wallet.TestUtils;
import org.leovegas.wallet.models.Operation;
import org.leovegas.wallet.models.domains.Transaction;
import org.leovegas.wallet.models.domains.Wallet;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.models.views.WalletOperationRequest;
import org.leovegas.wallet.models.views.WalletView;
import org.leovegas.wallet.repositories.WalletDao;
import org.leovegas.wallet.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.leovegas.wallet.TestUtils.*;
import static org.leovegas.wallet.utils.IdGenerator.*;

public class ConcurrencyChangeBalanceIT extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private WalletDao walletDao;

    @Test
    void name() throws InterruptedException {
        //given
        int playerId = generatePlayerId();
        WalletView walletView = walletDao.insert(new Wallet(
                generateWalletId(),
                playerId,
                BigInteger.ZERO,
                new BigInteger("100"),
                0
        )).mapTo(wallet -> new WalletView(
                playerId,
                wallet.balance()
        ));

        //when
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                webTestClient.post()
                        .uri("/players/{playerId}/wallet/debit", playerId)
                        .bodyValue(new WalletOperationRequest(generateTransactionId(), BigInteger.ONE))
                        .exchange();
                webTestClient.post()
                        .uri("/players/{playerId}/wallet/credit", playerId)
                        .bodyValue(new WalletOperationRequest(generateTransactionId(), BigInteger.ONE))
                        .exchange();

            });
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        //then
        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(walletView);

    }
}
