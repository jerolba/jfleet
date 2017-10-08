package org.jfleet.util;

public class CsvSplit {

    public static String[] split(String line, int fieldsNumber) {
        int col = 0;
        int idx = 0;
        String[] cols = new String[fieldsNumber];
        while (idx < line.length()) {
            if (line.charAt(idx)=='"') {
                idx++;
                int ini = idx;
                while (idx<line.length() && line.charAt(idx)!='"') {
                    idx++;
                }
                cols[col++] = line.substring(ini, idx);
                idx++; //Skip "
                idx++; //Skip ,
            }else {
                int ini = idx;
                while (idx<line.length() && line.charAt(idx)!=',') {
                    idx++;
                }
                cols[col++] = line.substring(ini, idx);
                idx++; //Skip ,
            }
        }
        return cols;
    }

}
