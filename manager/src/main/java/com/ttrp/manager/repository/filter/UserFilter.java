package com.ttrp.manager.repository.filter;

import com.ttrp.manager.dto.admin.AdminUserQuery;
import com.ttrp.manager.dto.queryFilter.QueryValue;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.UserRole;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

public class UserFilter {

    public static Specification<User> filterForUser(AdminUserQuery au_query){

        return (root, query, cb) -> {
            if(au_query == null || au_query.isEmpty()){
                return cb.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            List<Order> orders = new ArrayList<>();


            // Add search-rules for UserRoles
            addQueryPredicate(
                    predicates, orders, root, cb,
                    au_query.userRole(),
                    "userRole",
                    (path, roleQuery) ->
                    switch (roleQuery.operators()){
                        case NOT_IN -> cb.notEqual(path, roleQuery.value());
                        case EQUALS -> cb.equal(path, roleQuery.value());
                        default -> null;
            });


            // Add rules for email
            addQueryPredicate(
                    predicates, orders, root, cb,
                    au_query.email(),
                    "email",
                    (path, emailQuery) ->switch (emailQuery.operators()){
                        case LIKE -> cb.like(cb.lower(path.as(String.class)), getStringValue(emailQuery));
                        case EQUALS -> cb.equal(path, emailQuery.value());
                        default -> null;
            });


            // Add rules for accountStatus
            addQueryPredicate(
                    predicates, orders, root, cb,
                    au_query.accountStatus(),
                    "accountStatus",
                    (path, accountStatus) ->switch (accountStatus.operators()){
                        case NOT_EQUALS -> cb.notEqual(path, accountStatus.value());
                        case EQUALS -> cb.equal(path, accountStatus.value());
                        default -> null;
                    });


            // Add rules for username
            addQueryPredicate(
                    predicates, orders, root, cb,
                    au_query.username(),
                    "username",
                    (path,username) ->switch (username.operators()){
                        case EQUALS -> cb.equal(path, username.value());
                        case LIKE -> cb.like(cb.lower(path.as(String.class)), getStringValue(username));
                        default -> null;
                    });


            if(!orders.isEmpty()){
                query.orderBy(orders);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }




    private static <T> void addQueryPredicate(
            List<Predicate> predicates,
            List<Order> orders,
            Root<User> root,
            CriteriaBuilder cb,
            QueryValue<T> queryValue,
            String fieldName,
            BiFunction<Path<Object>, QueryValue<T>, Predicate> predicateBuilder
    ){
        if ( queryValue == null || !queryValue.hasValue()){
            return;
        }

        Path<Object> path = root.get(fieldName);

        Predicate predicate = predicateBuilder.apply(path, queryValue);
        if(predicate != null) {
            predicates.add(predicate);
        }

        if(queryValue.ascending() != null) {
            orders.add(queryValue.ascending() ? cb.asc(path) : cb.desc(path));
        }

    }


    private static String getStringValue(QueryValue<String> queryValue){
        return "%" + queryValue.value().trim().toLowerCase() + "%";
    }



}
