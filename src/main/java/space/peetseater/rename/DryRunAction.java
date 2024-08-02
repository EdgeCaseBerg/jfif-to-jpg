package space.peetseater.rename;

import java.util.logging.Logger;

public class DryRunAction<T> implements ConditionalConsumer<T> {

    public static Logger logger = Logger.getLogger(DryRunAction.class.toString());

    private final ConditionalConsumer<T> wrapped;

    public DryRunAction(ConditionalConsumer<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String toString() {
        return wrapped.toString() + " DRY RUN";
    }

    @Override
    public void accept(T t) {
        String wouldOrWouldNot = wrapped.shouldTakeAction(t) ? "" : "not ";
        String logMessage = "Would %stake action on %s with %s".formatted(wouldOrWouldNot, t, wrapped);
        logger.info(logMessage);
    }

    @Override
    public boolean shouldTakeAction(T t) {
        return wrapped.shouldTakeAction(t);
    }
}
