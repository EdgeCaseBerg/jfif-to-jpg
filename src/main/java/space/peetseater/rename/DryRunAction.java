package space.peetseater.rename;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class DryRunAction<T> implements Consumer<T> {

    public static Logger logger = Logger.getLogger(DryRunAction.class.toString());

    private final Consumer<T> wrapped;
    private final LinkedList<String> auditTrail;

    public DryRunAction(Consumer<T> wrapped) {
        this.wrapped = wrapped;
        this.auditTrail = new LinkedList<String>();
    }
    
    public void clearAuditTrail() {
        this.auditTrail.clear();
    }
    
    public LinkedList<String> getAuditTrail() {
        return this.auditTrail;
    }

    @Override
    public void accept(T t) {
        String logMessage = "Would take action on %s with %s".formatted(t, wrapped);
        logger.info(logMessage);
        this.auditTrail.add(logMessage);
    }
}
