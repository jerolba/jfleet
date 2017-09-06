package org.jfleet;

public class ComposedEntityFieldAccessor implements EntityFieldAccessor {

    private EntityFieldAccessor baseAccessor;
    private ComposedEntityFieldAccessor thenAccessor;

    public ComposedEntityFieldAccessor(EntityFieldAccessor accessor) {
        this.baseAccessor = accessor;
    }

    @Override
    public Object getValue(Object obj) {
        Object value = baseAccessor.getValue(obj);
        if (value != null) {
            if (thenAccessor != null) {
                Object next = thenAccessor.getValue(value);
                return next;
            }
            return value;
        }
        return null;
    }

    public ComposedEntityFieldAccessor andThen(EntityFieldAccessor then) {
        this.thenAccessor = new ComposedEntityFieldAccessor(then);
        return thenAccessor;
    }

}
