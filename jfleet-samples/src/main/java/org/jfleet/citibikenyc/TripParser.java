package org.jfleet.citibikenyc;

import org.jfleet.citibikenyc.entities.Trip;
import org.jfleet.citibikenyc.entities.Trip.Station;

class TripParser extends CSVParser<Trip> {

    public TripParser(String line) {
        super(line, 15);
    }

    @Override
    public Trip parse() {
        Trip trip = new Trip();
        trip.setTripDuration(nextInteger());
        trip.setStartTime(nextDate());
        trip.setStopTime(nextDate());
        Station start = new Station();
        start.setId(nextInteger());
        start.setName(nextString());
        start.setLatitude(nextDouble());
        start.setLongitude(nextDouble());
        trip.setStartStation(start);
        Station end = new Station();
        end.setId(nextInteger());
        end.setName(nextString());
        end.setLatitude(nextDouble());
        end.setLongitude(nextDouble());
        trip.setEndStation(end);
        trip.setBikeId(nextLong());
        trip.setUserType(nextString());
        trip.setBirthYear(nextInteger());
        trip.setGender(nextChar());
        return trip;
    }

}