package org.jfleet.avro;

public class TestEntity {
    private final String foo;
    private final Double fooDouble;
    private final Integer fooInt;

    public TestEntity(String foo, Double fooDouble, Integer fooInt) {
        this.foo = foo;
        this.fooDouble = fooDouble;
        this.fooInt = fooInt;
    }

    public String getFoo() {
        return foo;
    }

    public Double getFooDouble() {
        return fooDouble;
    }

    public Integer getFooInt() {
        return fooInt;
    }
}
