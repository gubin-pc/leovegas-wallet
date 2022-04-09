package org.leovegas.wallet.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.leovegas.wallet.AbstractIntegrationTest;
import org.leovegas.wallet.models.domains.Wallet;
import org.leovegas.wallet.models.views.WalletView;
import org.leovegas.wallet.repositories.WalletDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigInteger;

import static org.leovegas.wallet.TestUtils.generatePlayerId;
import static org.leovegas.wallet.utils.IdGenerator.generateWalletId;

class CurrentBalanceWalletControllerIT extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private WalletDao walletDao;

    @Test
    @DisplayName("should return balance for new user")
    void shouldReturnBalanceNewUser() {
        //given
        final int playerId = generatePlayerId();
        final WalletView expectedWalletView = new WalletView(playerId, BigInteger.ZERO);

        //then
        //when
        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(expectedWalletView);
    }

    @Test
    @DisplayName("should return balance for existed user")
    void shouldReturnBalanceExistedUser() {
        //given
        final int playerId = generatePlayerId();
        WalletView expectedWalletView = walletDao.insert(new Wallet(
                generateWalletId(),
                playerId,
                new BigInteger("29"),
                new BigInteger("45"),
                0
        )).mapTo(wallet -> new WalletView(
                playerId,
                wallet.balance()
        ));

        //when
        //then
        webTestClient.get()
                .uri("/players/{playerId}/wallet", playerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletView.class)
                .isEqualTo(expectedWalletView);
    }

    @Test
    @DisplayName("should return 400 if playerId is invalid")
    void shouldReturn400() {
        //given
        //then
        //when
        webTestClient.get()
                .uri("/players/{playerId}/wallet", "playerId")
                .exchange()
                .expectStatus().isBadRequest();
    }

}
