package com.lcx.service;


public interface ContestantService {

    String getSeatNum();

    String getSignNum();

    void waiverNatCompQual();

    void deleteContestant(String group, String zone);
}
