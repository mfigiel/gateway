package com.gateway.service;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TransactionSource {

    @Output("transactionChannel")
    MessageChannel sendTransaction();

}
