package org.jfleet;

public class ComposedEntityFieldAccessor implements EntityFieldAccessor {

    private final EntityFieldAccessor baseAccessor;
    private EntityFieldAccessor nextAccessor;

    public ComposedEntityFieldAccessor() {
        this(EntityFieldAccessor.identity());
    }

    private ComposedEntityFieldAccessor(EntityFieldAccessor baseAccessor) {
        this.baseAccessor = baseAccessor;
    }

    @Override
    public Object getValue(Object obj) {
        Object value = baseAccessor.getValue(obj);
        if (value != null) {
            return nextAccessor.getValue(value);
        }
        return null;
    }

    public ComposedEntityFieldAccessor andThen(EntityFieldAccessor then) {
        ComposedEntityFieldAccessor composed = new ComposedEntityFieldAccessor(then);
        this.nextAccessor = composed;
        return composed;
    }

    public void andFinally(EntityFieldAccessor accessor) {
        this.nextAccessor = accessor;
    }

}
