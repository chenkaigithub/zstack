package org.zstack.sdk;

import java.util.HashMap;
import java.util.Map;

public class QueryLdapServerAction extends QueryAction {

    private static final HashMap<String, Parameter> parameterMap = new HashMap<>();

    public static class Result {
        public ErrorCode error;
        public QueryLdapServerResult value;
    }



    public Result call() {
        ApiResult res = ZSClient.call(this);
        Result ret = new Result();
        if (res.error != null) {
            ret.error = res.error;
            return ret;
        }
        
        QueryLdapServerResult value = res.getResult(QueryLdapServerResult.class);
        ret.value = value == null ? new QueryLdapServerResult() : value;
        return ret;
    }

    public void call(final Completion<Result> completion) {
        ZSClient.call(this, new InternalCompletion() {
            @Override
            public void complete(ApiResult res) {
                Result ret = new Result();
                if (res.error != null) {
                    ret.error = res.error;
                    completion.complete(ret);
                    return;
                }
                
                QueryLdapServerResult value = res.getResult(QueryLdapServerResult.class);
                ret.value = value == null ? new QueryLdapServerResult() : value;
                completion.complete(ret);
            }
        });
    }

    Map<String, Parameter> getParameterMap() {
        return parameterMap;
    }

    RestInfo getRestInfo() {
        RestInfo info = new RestInfo();
        info.httpMethod = "GET";
        info.path = "/ldap/servers";
        info.needSession = true;
        info.needPoll = false;
        info.parameterName = "";
        return info;
    }

}