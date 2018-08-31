/**
 * Copyright 2017 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jfleet.samples.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfleet.util.CsvSplit;

//TODO: improve parse time
public abstract class CSVParser<T> {

    private final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("M/d/yyyy HH:mm:ss");

    private int col = 0;
    private final String[] cols;

    public abstract T parse();

    public CSVParser(String line, int fieldsNumber) {
        this.cols = CsvSplit.split(line, fieldsNumber);
    }

    private String next() {
        String str = cols[col++];
        if (str.equals("\\N")) {
            return null;
        }
        return str;
    }

    public String nextString() {
        return next();
    }

    public Character nextChar() {
        String str = next();
        if (str == null || str.length() == 0) {
            return null;
        }
        return str.charAt(0);
    }

    public Integer nextInteger() {
        String value = next();
        if (value != null && !value.equals("NULL") && value.length()>0) {
            return Integer.parseInt(value);
        }
        return null;
    }

    public Long nextLong() {
        String value = next();
        if (value != null) {
            return Long.parseLong(value);
        }
        return null;
    }

    public Double nextDouble() {
        String value = next();
        if (value != null) {
            return Double.parseDouble(value);
        }
        return null;
    }

    public Date nextDate() {
        try {
            String date = next();
            if (date.contains("-")) {
                return sdf1.parse(date);
            }else {
                return sdf2.parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
