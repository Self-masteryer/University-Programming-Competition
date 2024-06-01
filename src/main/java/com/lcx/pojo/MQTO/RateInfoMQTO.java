package com.lcx.pojo.MQTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateInfoMQTO {

    private int uid;
    private int jid;
    private int score;

}
