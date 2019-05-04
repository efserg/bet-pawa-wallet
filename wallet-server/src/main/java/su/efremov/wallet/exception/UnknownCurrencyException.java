package su.efremov.wallet.exception;

public class UnknownCurrencyException extends RuntimeException {
    public UnknownCurrencyException(String s) {
        super(s);
    }
}
