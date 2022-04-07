package org.leovegas.wallet.controllers;

import org.leovegas.wallet.models.Operation;
import org.leovegas.wallet.models.Status;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.models.views.WalletOperationRequest;
import org.leovegas.wallet.models.views.WalletView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/users")
public class WalletController {

    @GetMapping("{userId}/wallet")
    public WalletView getWallet(@PathVariable String userId) {
        return new WalletView(userId, BigInteger.ONE);
    }

    @GetMapping("{userId}/wallet/history")
    public List<TransactionView> getHistory(@PathVariable String userId) {
        return List.of(new TransactionView(
            userId,
                BigInteger.ONE,
                Operation.INCOME,
                Status.CANCELED,
                Instant.now()
        ));
    }

    @PostMapping("{userId}/wallet/credit")
    public TransactionView credit(
            @PathVariable String userId,
            @RequestBody WalletOperationRequest operationRequest
    ) {
        return new TransactionView(
                userId,
                operationRequest.sum(),
                Operation.INCOME,
                Status.SETTLED,
                Instant.now()
        );
    }

    @PostMapping("{userId}/wallet/debit")
    public TransactionView debit(
            @PathVariable String userId,
            @RequestBody WalletOperationRequest operationRequest
    ) {
        return new TransactionView(
                userId,
                operationRequest.sum(),
                Operation.OUTCOME,
                Status.SETTLED,
                Instant.now()
        );
    }
}
