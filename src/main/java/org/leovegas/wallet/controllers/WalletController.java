package org.leovegas.wallet.controllers;

import org.leovegas.wallet.models.Operation;
import org.leovegas.wallet.models.views.TransactionView;
import org.leovegas.wallet.models.views.WalletOperationRequest;
import org.leovegas.wallet.models.views.WalletView;
import org.leovegas.wallet.services.HistoryService;
import org.leovegas.wallet.services.TransactionService;
import org.leovegas.wallet.services.WalletOperationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static org.leovegas.wallet.utils.Preconditions.*;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/players")
public class WalletController {

    private final WalletOperationService walletOperationService;
    private final TransactionService transactionService;
    private final HistoryService historyService;

    public WalletController(WalletOperationService walletOperationService,
                            TransactionService transactionService,
                            HistoryService historyService) {
        this.walletOperationService = walletOperationService;
        this.transactionService = transactionService;
        this.historyService = historyService;
    }

    @GetMapping(value = "{playerId}/wallet", produces = APPLICATION_JSON_VALUE)
    public WalletView getWallet(@PathVariable final int playerId) {
        return walletOperationService.getOrCreateWallet(playerId)
                .mapTo(wallet -> new WalletView(wallet.playerId(), wallet.balance()));
    }

    @GetMapping(value = "{playerId}/wallet/history", produces = APPLICATION_JSON_VALUE)
    public List<TransactionView> getHistory(
            @PathVariable final int playerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate to
    ) {
        if (to != null) {
            require( from != null, () ->  "'to' date can't be use without 'from' date");
            require(from.isBefore(to), () ->  "'from' date should be before 'to' date");
            return historyService.getHistory(playerId, from, to);
        } else {
            return historyService.getHistory(playerId, from, null);
        }
    }

    @PostMapping(
            value = "{playerId}/wallet/credit",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public TransactionView credit(
            @PathVariable final int playerId,
            @RequestBody final WalletOperationRequest operationRequest
    ) {
        return transactionService.changePlayerBalance(
                playerId,
                operationRequest.transactionId(),
                operationRequest.amount(),
                Operation.INCOME
        );
    }

    @PostMapping(
            value = "{playerId}/wallet/debit",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public TransactionView debit(
            @PathVariable final int playerId,
            @RequestBody final WalletOperationRequest operationRequest
    ) {
        return transactionService.changePlayerBalance(
                playerId,
                operationRequest.transactionId(),
                operationRequest.amount(),
                Operation.OUTCOME
        );

    }
}
