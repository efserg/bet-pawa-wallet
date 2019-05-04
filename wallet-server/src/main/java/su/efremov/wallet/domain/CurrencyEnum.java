package su.efremov.wallet.domain;

import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.wallet.exception.UnknownCurrencyException;

public enum CurrencyEnum {
    EUR, USD, GBR;

    public static CurrencyEnum fromGrpc(Currency currency) {
        try {
            return CurrencyEnum.valueOf(currency.name());
        } catch (IllegalArgumentException ex) {
            throw new UnknownCurrencyException("Unknown currency: " + currency.name());
        }
    }
}
