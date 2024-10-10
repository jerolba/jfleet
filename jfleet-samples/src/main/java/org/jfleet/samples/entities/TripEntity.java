/**
 * Copyright 2022 Jerónimo López Bezanilla
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
package org.jfleet.samples.entities;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bike_trip")
public class TripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tripduration")
    private int tripduration;

    @Column(name = "starttime")
    private Date starttime;

    @Column(name = "stoptime")
    private Date stoptime;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "stationId", column = @Column(name = "start_station_id")),
        @AttributeOverride(name = "stationName", column = @Column(name = "start_station_name")),
        @AttributeOverride(name = "stationLatitude", column = @Column(name = "start_station_latitude")),
        @AttributeOverride(name = "stationLongitude", column = @Column(name = "start_station_longitude"))
    })
    private StationEmbedded startStation;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "stationId", column = @Column(name = "end_station_id")),
        @AttributeOverride(name = "stationName", column = @Column(name = "end_station_name")),
        @AttributeOverride(name = "stationLatitude", column = @Column(name = "end_station_latitude")),
        @AttributeOverride(name = "stationLongitude", column = @Column(name = "end_station_longitude"))
    })
    private StationEmbedded endStation;


    @Column(name = "bike_id")
    private long bikeId;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "gender")
    private Character gender;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTripduration() {
        return tripduration;
    }

    public void setTripduration(int tripduration) {
        this.tripduration = tripduration;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getStoptime() {
        return stoptime;
    }

    public void setStoptime(Date stoptime) {
        this.stoptime = stoptime;
    }

    public StationEmbedded getStartStation() {
        return startStation;
    }

    public void setStartStation(StationEmbedded startStation) {
        this.startStation = startStation;
    }

    public StationEmbedded getEndStation() {
        return endStation;
    }

    public void setEndStation(StationEmbedded endStation) {
        this.endStation = endStation;
    }

    public long getBikeId() {
        return bikeId;
    }

    public void setBikeId(long bikeId) {
        this.bikeId = bikeId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

}
