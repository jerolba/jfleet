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

public class TestEntityWithPrimitives {

  private int fooInt;
  private short fooShort;
  private byte fooByte;
  private double fooDouble;
  private long fooLong;
  private float fooFloat;
  private boolean fooBoolean;

  public int getFooInt() {
    return fooInt;
  }

  public void setFooInt(int fooInt) {
    this.fooInt = fooInt;
  }

  public short getFooShort() {
    return fooShort;
  }

  public void setFooShort(short fooShort) {
    this.fooShort = fooShort;
  }

  public byte getFooByte() {
    return fooByte;
  }

  public void setFooByte(byte fooByte) {
    this.fooByte = fooByte;
  }

  public double getFooDouble() {
    return fooDouble;
  }

  public void setFooDouble(double fooDouble) {
    this.fooDouble = fooDouble;
  }

  public long getFooLong() {
    return fooLong;
  }

  public void setFooLong(long fooLong) {
    this.fooLong = fooLong;
  }

  public float getFooFloat() {
    return fooFloat;
  }

  public void setFooFloat(float fooFloat) {
    this.fooFloat = fooFloat;
  }

  public boolean isFooBoolean() {
    return fooBoolean;
  }

  public void setFooBoolean(boolean fooBoolean) {
    this.fooBoolean = fooBoolean;
  }
}
