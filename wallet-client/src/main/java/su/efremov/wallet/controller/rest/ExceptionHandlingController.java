package su.efremov.wallet.controller.rest;

import static io.grpc.Status.FAILED_PRECONDITION;
import static io.grpc.Status.UNAVAILABLE;
import static su.efremov.wallet.controller.dto.ErrorCodes.INSUFFICIENT_FUNDS;
import static su.efremov.wallet.controller.dto.ErrorCodes.UNKNOWN_CURRENCY;
import static su.efremov.wallet.controller.dto.ErrorCodes.UNKNOWN_ERROR;
import static su.efremov.wallet.controller.dto.ErrorCodes.UNKNOWN_GRPC_SERVER_ERROR;

import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.grpc.StatusRuntimeException;
import su.efremov.wallet.controller.dto.ErrorDetails;

@RestControllerAdvice
public class ExceptionHandlingController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(StatusRuntimeException ex, WebRequest request) {
        ErrorDetails body = ErrorDetails.builder()
            .message(UNKNOWN_ERROR.getMessage())
            .code(UNKNOWN_ERROR.getCode())
            .description(ex.getMessage())
            .timestamp(Instant.now().getEpochSecond())
            .build();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(StatusRuntimeException.class)
    protected ResponseEntity<Object> handleGrpcServerException(StatusRuntimeException ex, WebRequest request) {

        if (UNAVAILABLE.getCode().equals(ex.getStatus().getCode())) {
            ErrorDetails body = ErrorDetails.builder()
                .message(INSUFFICIENT_FUNDS.getMessage())
                .code(INSUFFICIENT_FUNDS.getCode())
                .description(ex.getMessage())
                .timestamp(Instant.now().getEpochSecond())
                .build();
            return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        }

        if (FAILED_PRECONDITION.getCode().equals(ex.getStatus().getCode())) {
            ErrorDetails body = ErrorDetails.builder()
                .message(UNKNOWN_CURRENCY.getMessage())
                .code(UNKNOWN_CURRENCY.getCode())
                .description(ex.getMessage())
                .timestamp(Instant.now().getEpochSecond())
                .build();
            return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        }

        ErrorDetails body = ErrorDetails.builder()
            .message(UNKNOWN_GRPC_SERVER_ERROR.getMessage())
            .code(UNKNOWN_GRPC_SERVER_ERROR.getCode())
            .description(ex.getMessage())
            .timestamp(Instant.now().getEpochSecond())
            .build();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
