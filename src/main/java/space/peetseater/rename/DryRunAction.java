package space.peetseater.rename;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class DryRunAction<T> implements ConditionalConsumer<T> {

    public static Logger logger = Logger.getLogger(DryRunAction.class.toString());

    private final ConditionalConsumer<T> wrapped;
    private final LinkedList<Audit<T>> auditTrail;

    public DryRunAction(ConditionalConsumer<T> wrapped) {
        this.wrapped = wrapped;
        this.auditTrail = new LinkedList<Audit<T>>();
    }
    
    public void clearAuditTrail() {
        this.auditTrail.clear();
    }
    
    public LinkedList<Audit<T>> getAuditTrail() {
        return this.auditTrail;
    }

    @Override
    public void accept(T t) {
        String wouldOrWouldNot = wrapped.shouldTakeAction(t) ? "" : "not ";
        String logMessage = "Would %stake action on %s with %s".formatted(wouldOrWouldNot, t, wrapped);
        logger.info(logMessage);
        this.auditTrail.add(new Audit<>(t, logMessage));
    }

    @Override
    public boolean shouldTakeAction(T t) {
        return wrapped.shouldTakeAction(t);
    }
}
