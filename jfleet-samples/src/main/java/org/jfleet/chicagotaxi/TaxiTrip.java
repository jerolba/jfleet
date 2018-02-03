package org.jfleet.chicagotaxi;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "taxi_trip")
public class TaxiTrip {

    @Id
    private String id;

    @Column(name = "taxi_id")
    private String taxiId;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "time_seconds")
    private Integer timeSeconds;

    @Column(name = "distance_miles")
    private Float distanceMiles;

    @Column(name = "pickup_tract")
    private Long pickupTract;

    @Column(name = "pickup_community")
    private Long pickupCommunity;

    @Column(name = "dropoff_tract")
    private Long dropoffTract;

    @Column(name = "dropoff_community")
    private Long dropoffCommunity;

    @Column(name = "fare")
    private Float fare;

    @Column(name = "tips")
    private Float tips;

    @Column(name = "tolls")
    private Float tolls;

    @Column(name = "extras")
    private Float extras;

    @Column(name = "total")
    private Float total;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "company")
    private String company;

    @Column(name = "pickup_latitude")
    private Double pickupLatitude;

    @Column(name = "pickup_longitude")
    private Double pickupLongitude;

    @Column(name = "dropoff_latitude")
    private Double dropoffLatitude;

    @Column(name = "dropoff_longitude")
    private Double dropoffLongitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(String taxiId) {
        this.taxiId = taxiId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(Integer timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    public Float getDistanceMiles() {
        return distanceMiles;
    }

    public void setDistanceMiles(Float distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public Long getPickupTract() {
        return pickupTract;
    }

    public void setPickupTract(Long pickupTract) {
        this.pickupTract = pickupTract;
    }

    public Long getPickupCommunity() {
        return pickupCommunity;
    }

    public void setPickupCommunity(Long pickupCommunity) {
        this.pickupCommunity = pickupCommunity;
    }

    public Long getDropoffTract() {
        return dropoffTract;
    }

    public void setDropoffTract(Long dropoffTract) {
        this.dropoffTract = dropoffTract;
    }

    public Long getDropoffCommunity() {
        return dropoffCommunity;
    }

    public void setDropoffCommunity(Long dropoffCommunity) {
        this.dropoffCommunity = dropoffCommunity;
    }

    public Float getFare() {
        return fare;
    }

    public void setFare(Float fare) {
        this.fare = fare;
    }

    public Float getTips() {
        return tips;
    }

    public void setTips(Float tips) {
        this.tips = tips;
    }

    public Float getTolls() {
        return tolls;
    }

    public void setTolls(Float tolls) {
        this.tolls = tolls;
    }

    public Float getExtras() {
        return extras;
    }

    public void setExtras(Float extras) {
        this.extras = extras;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public Double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public Double getDropoffLatitude() {
        return dropoffLatitude;
    }

    public void setDropoffLatitude(Double dropoffLatitude) {
        this.dropoffLatitude = dropoffLatitude;
    }

    public Double getDropoffLongitude() {
        return dropoffLongitude;
    }

    public void setDropoffLongitude(Double dropoffLongitude) {
        this.dropoffLongitude = dropoffLongitude;
    }

}
