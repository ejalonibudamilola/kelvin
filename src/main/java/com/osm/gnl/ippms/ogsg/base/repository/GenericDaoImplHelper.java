/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.repository;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.ConjunctionType;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

public class GenericDaoImplHelper {

    protected static Predicate preparePredicate(Predicate where, CriteriaBuilder builder, @NotNull CustomPredicate predicate, Root<?> root) {

        if(predicate.getOperation().equals(Operation.EQUALS)){
            if(predicate.getConjunctionType() != null){
                if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                    where = builder.or(where, builder.equal(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                }else{
                    where = builder.and(where, builder.equal(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                }
            }else{
                where = builder.and(where, builder.equal(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
            }

        }else {
            if (predicate.getOperation().equals(Operation.GREATER)) {
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        where = builder.or(where, builder.greaterThan(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }else{
                        where = builder.and(where, builder.greaterThan(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }
                }else{
                    where = builder.and(where, builder.greaterThan(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                }

            } else if (predicate.getOperation().equals(Operation.GREATER_OR_EQUAL)) {
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        where = builder.or(where, builder.greaterThanOrEqualTo(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }else{
                        where = builder.and(where, builder.greaterThanOrEqualTo(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }
                }else{
                    where = builder.and(where, builder.greaterThanOrEqualTo(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                }

            } else if (predicate.getOperation().equals(Operation.LIKE)) {
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        if (((String) predicate.getValue()).indexOf("%") == -1) {
                            where = builder.or(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), "%" + ((String) predicate.getValue()).toUpperCase().trim() + "%"));
                        } else {
                            where = builder.or(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), ((String) predicate.getValue()).toUpperCase().trim()));
                        }
                    }else{
                        if (((String) predicate.getValue()).indexOf("%") == -1) {
                            where = builder.and(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), "%" + ((String) predicate.getValue()).toUpperCase().trim() + "%"));
                        } else {
                            where = builder.and(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), ((String) predicate.getValue()).toUpperCase().trim()));
                        }
                    }
                }else{
                    if (((String) predicate.getValue()).indexOf("%") == -1) {
                        where = builder.and(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), "%" + ((String) predicate.getValue()).toUpperCase().trim() + "%"));
                    } else {
                        where = builder.and(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), ((String) predicate.getValue()).toUpperCase().trim()));
                    }
                }

            } else if (predicate.getOperation().equals(Operation.STRING_EQUALS)){
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        where = builder.or(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), ((String) predicate.getValue()).toUpperCase().trim()));
                    }else{
                        where = builder.and(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), ((String) predicate.getValue()).toUpperCase().trim()));
                    }
                }else{
                    where = builder.and(where, builder.like(builder.trim(builder.upper(PredicateBuilder.getPath(predicate, root))), ((String) predicate.getValue()).toUpperCase().trim()));
                }
            }   else if (predicate.getOperation().equals(Operation.LESS)) {
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        where = builder.or(where, builder.lessThan(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }else{
                        where = builder.and(where, builder.lessThan(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }
                }else{
                    where = builder.and(where, builder.lessThan(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                }
            } else if (predicate.getOperation().equals(Operation.LESS_OR_EQUAL)) {
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        where = builder.or(where, builder.lessThanOrEqualTo(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }else{
                        where = builder.and(where, builder.lessThanOrEqualTo(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }
                }else{
                    where = builder.and(where, builder.lessThanOrEqualTo(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                }
            } else if (predicate.getOperation().equals(Operation.NOT_EQUAL)) {
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        where = builder.or(where, builder.notEqual(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }else{
                        where = builder.and(where, builder.notEqual(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                    }
                }else{
                    where = builder.and(where, builder.notEqual(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                }
            } else if (predicate.getOperation().equals(Operation.BETWEEN)) {
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        where = builder.or(where, builder.between(PredicateBuilder.getPath(predicate, root), predicate.getValue(), predicate.getOtherValues().get(0)));
                    }else{
                        where = builder.and(where, builder.between(PredicateBuilder.getPath(predicate, root), predicate.getValue(), predicate.getOtherValues().get(0)));
                    }
                }else{
                    where = builder.and(where, builder.between(PredicateBuilder.getPath(predicate, root), predicate.getValue(), predicate.getOtherValues().get(0)));
                }
            }else if(predicate.getOperation().equals(Operation.IS_NULL)){
                if(predicate.getConjunctionType() != null){
                    if(predicate.getConjunctionType().equals(ConjunctionType.OR)){
                        where = builder.or(where, builder.isNull(PredicateBuilder.getPath(predicate, root)));
                    }else{
                        where = builder.and(where, builder.isNull(PredicateBuilder.getPath(predicate, root)));
                    }
                }else{
                    where = builder.and(where, builder.isNull(PredicateBuilder.getPath(predicate, root)));
                }
            }
        }
        return where;
    }
}
