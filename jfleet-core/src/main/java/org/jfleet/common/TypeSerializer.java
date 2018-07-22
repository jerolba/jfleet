package org.jfleet.common;

import org.jfleet.EntityFieldType;

public interface TypeSerializer {

    String toString(Object obj, EntityFieldType entityFieldType);

}
