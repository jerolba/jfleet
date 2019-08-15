package org.jfleet.parameterized;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(IsMySql5Condition.class)
public @interface IsMySql5Present {

}
