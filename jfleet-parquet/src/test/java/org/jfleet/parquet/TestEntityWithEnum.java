package org.jfleet.parquet;

public class TestEntityWithEnum {

  private WeekDays foo;
  private WeekDays bar;

  public WeekDays getFoo() {
    return foo;
  }

  public void setFoo(WeekDays foo) {
    this.foo = foo;
  }

  public WeekDays getBar() {
    return bar;
  }

  public void setBar(WeekDays bar) {
    this.bar = bar;
  }

  public enum WeekDays {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
  }
}
