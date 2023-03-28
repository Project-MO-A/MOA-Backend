package com.moa.domain.member;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public enum Attendance {
    ATTENDANCE,
    NONATTENDANCE,
    NONE;

    public static Map<String, List<String>> attendanceMap() {
        return Arrays.stream(Attendance.values())
                .collect(toMap(Enum::name, value -> new ArrayList<>()));
    }
}
