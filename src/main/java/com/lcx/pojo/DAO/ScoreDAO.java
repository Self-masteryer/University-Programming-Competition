package com.lcx.pojo.DAO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoreDAO {

    private float writtenScore;
    private float practicalScore;
    private float qAndAScore;

}
