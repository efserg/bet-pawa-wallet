package su.efremov.wallet.controller.rest;

import static java.util.stream.Collectors.toList;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import su.efremov.bet.pawa.balance.BalanceRequest;
import su.efremov.bet.pawa.deposit.AddFundsRequest;
import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.bet.pawa.withdraw.WithdrawRequest;
import su.efremov.wallet.controller.dto.CurrencyEnum;
import su.efremov.wallet.controller.dto.request.AddFundsRestRequest;
import su.efremov.wallet.controller.dto.request.WithdrawRestRequest;
import su.efremov.wallet.controller.dto.response.BalanceRestResponse;
import su.efremov.wallet.service.BalanceClientService;
import su.efremov.wallet.service.DepositClientService;
import su.efremov.wallet.service.WithdrawClientService;

@RestController
@AllArgsConstructor
public class WalletController {

    private final DepositClientService depositService;

    private final WithdrawClientService withdrawService;

    private final BalanceClientService balanceService;

    @PostMapping("addFunds/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFunds(@PathVariable Long userId, @RequestBody AddFundsRestRequest request) {
        depositService.addFunds(AddFundsRequest.newBuilder()
            .setUserId(userId)
            .setCurrency(Currency.valueOf(request.getCurrency().name()))
            .setAmount(request.getAmount().toString())
            .build());
    }

    @PostMapping("withdraw/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@PathVariable Long userId, @RequestBody WithdrawRestRequest request) {
        withdrawService.withdraw(WithdrawRequest.newBuilder()
            .setUserId(userId)
            .setCurrency(Currency.valueOf(request.getCurrency().name()))
            .setAmount(request.getAmount().toString())
            .build());
    }

    @PostMapping("balance/{userId}")
    public ResponseEntity<BalanceRestResponse> addFunds(@PathVariable Long userId) {
        List<BalanceRestResponse.CurrencyBalance> balances = balanceService.balance(BalanceRequest.newBuilder()
            .setUserId(userId)
            .build()).getBalanceList().stream()
            .map(balance -> BalanceRestResponse.CurrencyBalance.builder()
//                .amount(BigDecimal.valueOf(balance.getAmount())) // todo: new gRPC type Money
                .currency(CurrencyEnum.valueOf(balance.getCurrency().name()))
                .build())
            .collect(toList());
        return ResponseEntity.ok(BalanceRestResponse.builder()
            .balances(balances)
            .build());
    }
}
