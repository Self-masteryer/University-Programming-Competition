package com.lcx.common.constant;

public class Process {

    public static final String[] PROCESS_STEP = {"written:seat_draw", "written:post_written_score",
            "written:score_filter", "written:next", "practice:group_draw", "practice:rate", "practice:next",
            "q_and_a:rate", "q_and_a:next", "final:score_export", "final:next"};

    public static final String WRITTEN = "written";
    public static final String PRACTICE = "practice";
    public static final String Q_AND_A = "q_and_a";
    public static final String FINAL = "final";

    public static final String DISTRICT = "district";
    public static final String NATIONAL = "national";

    public static final String W = "笔试环节";
    public static final String P = "实战能力比试";
    public static final String Q = "快问快答环节";
    public static final String F = "成绩现场导出";
}
