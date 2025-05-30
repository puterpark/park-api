package us.puter.park.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import us.puter.park.common.filter.PrimaryRoutingFilter;

@Slf4j
public class CustomRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        if (PrimaryRoutingFilter.isForcedPrimary()) {
            log.trace("Current DataSource[forced-primary]");
            return "primary";
        }

        String lookupKey = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "replica" : "primary";
        log.trace("Current DataSource[{}]", lookupKey);

        return lookupKey;
    }
}
