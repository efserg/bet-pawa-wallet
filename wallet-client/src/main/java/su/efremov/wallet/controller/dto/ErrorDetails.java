package su.efremov.wallet.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ErrorDetails {

    private Integer code;

    private String description;

    private String message;

    private Long timestamp;

}
