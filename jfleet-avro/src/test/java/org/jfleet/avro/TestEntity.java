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
package org.jfleet.avro;

public class TestEntity {

    private String fooString;
    private Integer fooInt;
    private Short fooShort;
    private Byte fooByte;
    private Double fooDouble;
    private Long fooLong;
    private Float fooFloat;
    private Boolean fooBoolean;

    public String getFooString() {
        return fooString;
    }

    public void setFooString(String fooString) {
        this.fooString = fooString;
    }

    public Integer getFooInt() {
        return fooInt;
    }

    public void setFooInt(Integer fooInt) {
        this.fooInt = fooInt;
    }

    public Short getFooShort() {
        return fooShort;
    }

    public void setFooShort(Short fooShort) {
        this.fooShort = fooShort;
    }

    public Byte getFooByte() {
        return fooByte;
    }

    public void setFooByte(Byte fooByte) {
        this.fooByte = fooByte;
    }

    public Double getFooDouble() {
        return fooDouble;
    }

    public void setFooDouble(Double fooDouble) {
        this.fooDouble = fooDouble;
    }

    public Long getFooLong() {
        return fooLong;
    }

    public void setFooLong(Long fooLong) {
        this.fooLong = fooLong;
    }

    public Float getFooFloat() {
        return fooFloat;
    }

    public void setFooFloat(Float fooFloat) {
        this.fooFloat = fooFloat;
    }

    public Boolean getFooBoolean() {
        return fooBoolean;
    }

    public void setFooBoolean(Boolean fooBoolean) {
        this.fooBoolean = fooBoolean;
    }

}
