package space.peetseater.rename;

import java.util.function.Consumer;

public interface ConditionalConsumer<T> extends Consumer<T> {
    boolean shouldTakeAction(T t);
    default Audit<T> getAudit(T t) {
        String w = shouldTakeAction(t) ? "Would" : "Would not";
        return new Audit<>(t, "%s %s on %s".formatted(w, toString(), t));
    }
}
