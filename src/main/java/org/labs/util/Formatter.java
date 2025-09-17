package org.labs.util;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.StringJoiner;

public class Formatter {
    public static String formatSimulationResultMap(Map<Integer, Integer> map) {
        StringJoiner sj = new StringJoiner(
                "\n",
                "\n id | consumed\n",
                "\n");
        DecimalFormat formatter = new DecimalFormat("000");
        for (var entry : map.entrySet()) {
            sj.add(formatter.format(entry.getKey()) + " | " + entry.getValue());
        }
        return sj.toString();
    }
}
