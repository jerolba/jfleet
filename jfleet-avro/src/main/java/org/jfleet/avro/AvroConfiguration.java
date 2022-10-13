package org.jfleet.avro;

import org.jfleet.EntityInfo;

public class AvroConfiguration<T> {

    private EntityInfo entityInfo;
    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public AvroConfiguration(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
        clazz = (Class<T>) entityInfo.getEntityClass();
    }

    public AvroConfiguration(Class<T> clazz) {
        this.clazz = clazz;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
