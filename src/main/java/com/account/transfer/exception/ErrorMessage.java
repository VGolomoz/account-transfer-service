package com.account.transfer.exception;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Data
public class ErrorMessage {

    private final String key;
    private final String text;

}