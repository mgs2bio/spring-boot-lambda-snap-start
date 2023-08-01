package com.amrut.prabhu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Confirmation {

    private String orderId;
    private boolean isShipped;
    private long balance;
}
