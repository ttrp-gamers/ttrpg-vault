package com.ttrp.manager.dto.queryFilter;

public record QueryValue<T>(
        T value,
        QueryOperators operators,
        Boolean ascending
) {
    public QueryValue {
        if(operators == null){
            operators = QueryOperators.EQUALS;
        }
        if(ascending == null){
            ascending = true;
        }
    }

    public static <T> QueryValue<T> of(T value){
        return new QueryValue<T>(value, QueryOperators.EQUALS, true);
    }

    public boolean hasValue(){
        return value!=null;
    }

}
