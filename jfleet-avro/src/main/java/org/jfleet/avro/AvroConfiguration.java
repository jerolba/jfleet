package org.jfleet.avro;

import org.jfleet.EntityInfo;

public class AvroConfiguration {
    private final EntityInfo entityInfo;

    public AvroConfiguration(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }
}
