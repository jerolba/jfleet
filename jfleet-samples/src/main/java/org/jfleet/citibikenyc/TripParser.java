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
package org.jfleet.citibikenyc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//TODO: improve parse time
public class TripParser {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final String[] cols;
	private int col = 0;

	public TripParser(String line) {
		int idx = 0;
		ArrayList<String> list = new ArrayList<>();
		while (idx < line.length()) {
			if (line.charAt(idx)=='"') {
				idx++;
				int ini = idx;
				while (idx<line.length() && line.charAt(idx)!='"') {
					idx++;
				}
				list.add(line.substring(ini, idx));
				idx++; //Skip "
				idx++; //Skip ,
			}else {
				int ini = idx;
				while (idx<line.length() && line.charAt(idx)!=',') {
					idx++;
				}
				list.add(line.substring(ini, idx));
				idx++; //Skip ,
			}
		}
		this.cols = list.toArray(new String[list.size()]);
	}

	public TripEntity parse() {
		TripEntity trip = new TripEntity();
		trip.setTripduration(nextInteger());
		trip.setStarttime(nextDate());
		trip.setStoptime(nextDate());
		trip.setStartStationId(nextInteger());
		trip.setStartStationName(nextString());
		trip.setStartStationLatitude(nextDouble());
		trip.setStartStationLongitude(nextDouble());
		trip.setEndStationId(nextInteger());
		trip.setEndStationName(nextString());
		trip.setEndStationLatitude(nextDouble());
		trip.setEndStationLongitude(nextDouble());
		trip.setBikeId(nextLong());
		trip.setUserType(nextString());
		trip.setBirthYear(nextInteger());
		trip.setGender(nextChar());
		return trip;
	}

	private String next() {
		String str = cols[col++];
		if (str.equals("\\N")) {
			return null;
		}
		return str;
	}

	private String nextString() {
		return next();
	}

	private Character nextChar() {
		String str = next();
		if (str == null || str.length() == 0) {
			return null;
		}
		return str.charAt(0);
	}

	private Integer nextInteger() {
		String value = next();
		if (value != null && !value.equals("NULL")) {
			return Integer.parseInt(value);
		}
		return null;
	}

	private Long nextLong() {
		String value = next();
		if (value != null) {
			return Long.parseLong(value);
		}
		return null;
	}

	private Double nextDouble() {
		String value = next();
		if (value != null) {
			return Double.parseDouble(value);
		}
		return null;
	}

	private Date nextDate() {
		try {
			return sdf.parse(next());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}