package com.ttrp.manager.dto.queryFilter;

public record QueryValue<T>(
        T value,
        QueryOperators operators,
        Boolean ascending
) {
    public static <T> QueryValue<T> of(T value){
        return new QueryValue<T>(value, QueryOperators.EQUALS, true);
    }

    public boolean hasValue(){
        return value!=null;
    }

}
