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
package org.jfleet.citibikenyc.csv;

import java.util.Date;

public class Trip {

    private int tripduration;

    private Date starttime;

    private Date stoptime;

    private int startStationId;

    private String startStationName;

    private double startStationLatitude;

    private double startStationLongitude;

    private int endStationId;

    private String endStationName;

    private double endStationLatitude;

    private double endStationLongitude;

    private long bikeId;

    private String userType;

    private Integer birthYear;

    private Character gender;

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

    public int getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(int startStationId) {
        this.startStationId = startStationId;
    }

    public String getStartStationName() {
        return startStationName;
    }

    public void setStartStationName(String startStationName) {
        this.startStationName = startStationName;
    }

    public double getStartStationLatitude() {
        return startStationLatitude;
    }

    public void setStartStationLatitude(double startStationLatitude) {
        this.startStationLatitude = startStationLatitude;
    }

    public double getStartStationLongitude() {
        return startStationLongitude;
    }

    public void setStartStationLongitude(double startStationLongitude) {
        this.startStationLongitude = startStationLongitude;
    }

    public int getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(int endStationId) {
        this.endStationId = endStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    public double getEndStationLatitude() {
        return endStationLatitude;
    }

    public void setEndStationLatitude(double endStationLatitude) {
        this.endStationLatitude = endStationLatitude;
    }

    public double getEndStationLongitude() {
        return endStationLongitude;
    }

    public void setEndStationLongitude(double endStationLongitude) {
        this.endStationLongitude = endStationLongitude;
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
