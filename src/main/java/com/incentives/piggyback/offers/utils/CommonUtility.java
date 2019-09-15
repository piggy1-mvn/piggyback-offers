package com.incentives.piggyback.offers.utils;

public class CommonUtility {
    public static String stringifyEventForPublish(String param1, String param2, String param3, String param4, String param5) {
        StringBuilder builder = new StringBuilder();
        builder.append(param1).append(";").append(param2).append(";").append(param3).append(";").append(param4).append(";").append(param5);
        return builder.toString();
    }
}
