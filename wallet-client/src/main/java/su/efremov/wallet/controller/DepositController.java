package su.efremov.wallet.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import su.efremov.bet.pawa.deposit.AddFundsRequest;
import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.wallet.controller.request.AddFundsRestRequest;
import su.efremov.wallet.service.DepositClientService;

@RestController("deposit")
@AllArgsConstructor
public class DepositController {

    private final DepositClientService depositClientService;

    @PostMapping("add")
    @ResponseStatus(HttpStatus.OK)
    public void addFunds(@RequestBody AddFundsRestRequest request) {
        depositClientService.addFunds(AddFundsRequest.newBuilder()
            .setUserId(request.getUserId())
            .setCurrency(Currency.valueOf(request.getCurrency().name()))
            .setAmount(request.getAmount().toString())
            .build());
    }
}
