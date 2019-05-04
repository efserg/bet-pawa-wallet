package su.efremov.wallet.controller.dto;

public enum ErrorCodes {

    INSUFFICIENT_FUNDS(100, "Insufficient funds"),
    UNKNOWN_CURRENCY(105, "Unknown currency"),
    UNKNOWN_GRPC_SERVER_ERROR(998, "Unknown gRPC server error"),
    UNKNOWN_ERROR(999, "Unknown error");

    private int code;

    private String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
