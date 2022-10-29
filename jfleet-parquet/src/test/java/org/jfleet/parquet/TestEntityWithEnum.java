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
