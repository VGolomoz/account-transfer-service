package com.account.transfer.api.dto;

import com.account.transfer.exception.ErrorMessage;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {

    String errorCode;
    ErrorMessage errorMessage;
    long timestamp;
}
