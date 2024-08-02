package space.peetseater.rename;

import java.util.function.Consumer;

public interface ConditionalConsumer<T> extends Consumer<T> {
    boolean shouldTakeAction(T t);
}
