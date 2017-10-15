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
package org.jfleet.citibikenyc.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bike_trip")
public class TripFlatEntity {

    @Id
    /* Removed in JPA because disable batch inserts. He must get created IDs to update the entity
     * and main the identity of entity in memory. You can also use a logical composite key.
     */
	//@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="tripduration")
	private int tripduration;

	@Column(name="starttime")
	private Date starttime;

	@Column(name="stoptime")
	private Date stoptime;

	@Column(name="start_station_id")
	private int startStationId;

	@Column(name="start_station_name")
	private String startStationName;

	@Column(name="start_station_latitude")
	private double startStationLatitude;

	@Column(name="start_station_longitude")
	private double startStationLongitude;

	@Column(name="end_station_id")
	private int endStationId;

	@Column(name="end_station_name")
	private String endStationName;

	@Column(name="end_station_latitude")
	private double endStationLatitude;

	@Column(name="end_station_longitude")
	private double endStationLongitude;

	@Column(name="bike_id")
	private long bikeId;

	@Column(name="user_type")
	private String userType;

	@Column(name="birth_year")
	private Integer birthYear;

	@Column(name="gender")
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
