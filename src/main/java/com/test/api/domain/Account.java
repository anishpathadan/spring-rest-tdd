package com.test.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Wither
public class Account {
    String id;
    String accountNumber;
    String accountName;
    String accountType;
    boolean isActive;
}
