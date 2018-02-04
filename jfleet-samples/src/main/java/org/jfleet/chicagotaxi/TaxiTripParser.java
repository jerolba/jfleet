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
package org.jfleet.chicagotaxi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfleet.util.CsvSplit;

public class TaxiTripParser {

    private final SimpleDateFormat tripTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    public TaxiTrip parse(String line) {
        String[] columns = CsvSplit.split(line, 24);
        TaxiTrip trip = new TaxiTrip();
        trip.setId(columns[0]);
        trip.setTaxiId(columns[1]);
        trip.setStartTime(parseTime(columns[2]));
        trip.setEndTime(parseTime(columns[3]));
        trip.setTimeSeconds(parseInt(columns[4]));
        trip.setDistanceMiles(parseFloat(columns[5]));
        trip.setPickupTract(parseLong(columns[6]));
        trip.setPickupCommunity(parseLong(columns[7]));
        trip.setDropoffTract(parseLong(columns[8]));
        trip.setDropoffCommunity(parseLong(columns[9]));
        trip.setFare(parseAmount(columns[10]));
        trip.setTips(parseAmount(columns[11]));
        trip.setTolls(parseAmount(columns[12]));
        trip.setExtras(parseAmount(columns[13]));
        trip.setTotal(parseAmount(columns[14]));
        trip.setPaymentType(columns[15]);
        trip.setCompany(columns[16]);
        trip.setPickupLatitude(parseDouble(columns[17]));
        trip.setPickupLongitude(parseDouble(columns[18]));
        trip.setDropoffLatitude(parseDouble(columns[20]));
        trip.setDropoffLongitude(parseDouble(columns[21]));
        return trip;
    }

    private Date parseTime(String value) {
        if (value!=null && value.length()>0) {
            try {
                return tripTimeFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Float parseAmount(String value) {
        if (value.length()>0) {
            return parseFloat(value.substring(2));
        }
        return null;
    }

    private Float parseFloat(String value) {
        if (value!=null && value.length()>0) {
            return Float.parseFloat(value);
        }
        return null;
    }

    private Long parseLong(String value) {
        if (value!=null && value.length()>0) {
            return Long.parseLong(value);
        }
        return null;
    }

    private Integer parseInt(String value) {
        if (value!=null && value.length()>0) {
            return Integer.parseInt(value);
        }
        return null;
    }

    private Double parseDouble(String value) {
        if (value!=null && value.length()>0) {
            return Double.parseDouble(value);
        }
        return null;
    }

}
