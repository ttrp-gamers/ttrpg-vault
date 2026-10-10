package com.ttrp.manager.dto.admin;

import com.ttrp.manager.dto.queryFilter.QueryValue;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;

public record AdminUserQuery(
        QueryValue<UserRole> userRole,
        QueryValue<AccountStatus> accountStatus,
        QueryValue<String> username,
        QueryValue<String> email
) {
    public boolean isEmpty(){
        return !(exists(userRole)
                || exists(accountStatus)
                || exists(username)
                || exists(email));
    }

    public boolean  exists(QueryValue<?> qv){
        return qv !=null && qv.hasValue();
    }

}
