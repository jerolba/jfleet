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

import org.jfleet.citibikenyc.entities.StationEmbedded;
import org.jfleet.citibikenyc.entities.TripEntity;

public class TripParser extends CSVParser<TripEntity> {

	public TripParser(String line) {
	    super(line, 15);
	}

	@Override
    public TripEntity parse() {
		TripEntity trip = new TripEntity();
		trip.setTripduration(nextInteger());
		trip.setStarttime(nextDate());
		trip.setStoptime(nextDate());
		StationEmbedded startStation = new StationEmbedded();
		startStation.setStationId(nextInteger());
		startStation.setStationName(nextString());
		startStation.setStationLatitude(nextDouble());
		startStation.setStationLongitude(nextDouble());
		trip.setStartStation(startStation);
		StationEmbedded endStation = new StationEmbedded();
		endStation.setStationId(nextInteger());
		endStation.setStationName(nextString());
		endStation.setStationLatitude(nextDouble());
		endStation.setStationLongitude(nextDouble());
		trip.setEndStation(endStation);
		trip.setBikeId(nextLong());
		trip.setUserType(nextString());
		trip.setBirthYear(nextInteger());
		trip.setGender(nextChar());
		return trip;
	}

}