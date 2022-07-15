package org.jfleet.avro;

public class TestEntity {
    private final String foo;
    private final String bar;

    public TestEntity(String foo, String bar) {
        this.foo = foo;
        this.bar = bar;
    }

    public String getFoo() {
        return foo;
    }

    public String getBar() {
        return bar;
    }
}
