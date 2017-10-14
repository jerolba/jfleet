package org.jfleet.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "city")
    private City city;

    public Employee(Integer id, String name, City city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public City getCity() {
        return city;
    }

}