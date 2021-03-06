package org.zstack.identity;

import org.zstack.core.cloudbus.CloudBusGson;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.identity.extension.AuthorizationBackend;
import org.zstack.header.message.APIMessage;
import org.zstack.identity.rbac.OperationTargetAPIRequestChecker;
import org.zstack.identity.rbac.RBACAPIRequestChecker;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.ArrayList;
import java.util.List;

public class DefaultAuthorizationBackend implements AuthorizationBackend {
    private static final CLogger logger = Utils.getLogger(DefaultAuthorizationBackend.class);

    @Override
    public boolean takeOverAuthorization(SessionInventory session) {
        return true;
    }

    @Override
    public APIMessage authorize(APIMessage msg) {
        List<APIRequestChecker> checkers = new ArrayList<>();
        checkers.add(new RBACAPIRequestChecker());
        checkers.add(new OperationTargetAPIRequestChecker());
        checkers.add(new QuotaAPIRequestChecker());

        try {
            checkers.forEach(c -> {
                if (!c.bypass(msg)) {
                    c.check(msg);
                }
            });
        } catch (OperationFailureException e) {
            if (logger.isTraceEnabled()) {
                logger.trace(String.format("%s, %s", e.getErrorCode(), CloudBusGson.toJson(msg)));
            }

            throw e;
        }

        return msg;
    }
}
