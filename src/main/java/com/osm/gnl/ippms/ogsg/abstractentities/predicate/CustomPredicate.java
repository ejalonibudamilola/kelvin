package com.osm.gnl.ippms.ogsg.abstractentities.predicate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomPredicate {
    private String field;
    private Comparable value;
    private List<Comparable> otherValues;
    private Operation operation;
    private ConjunctionType conjunctionType;
    private boolean negate;
    private static CustomPredicate instance;


    /**
     *
     * @param field
     * @param value
     */

    public static CustomPredicate procurePredicate(String field, Comparable value){
        return buildCustomPredicate( field, value, Operation.EQUALS,false, null);
    }
    public static CustomPredicate procurePredicate(String field, Comparable value, Operation operation){
        return buildCustomPredicate( field, value, operation,false, null);
    }
    public static CustomPredicate procurePredicate(String field, Comparable value, Operation operation, boolean negate){
        return buildCustomPredicate( field, value, operation,negate, null);
    }
    public static CustomPredicate procurePredicate(String field, Comparable value, Operation operation,ConjunctionType conjunctionType){
        return buildCustomPredicate( field, value,operation,false,conjunctionType);
    }



    @Deprecated
    /**
     *  @deprecated
     *
     *  @see Use CustomPredicate.procurePredicate(String s,Comparable c)
     *                Support will be deleted May 4th 2021.
     * @param field
     * @param value
     */
     public CustomPredicate(String field, Comparable value) {
        this(field, value, Operation.EQUALS);
    }
    /**
     *  @deprecated  : Use CustomPredicate.procurePredicate(String s,Comparable c, Operation o)
     *                Support will be deleted May 4th 2021.
     * @param field
     * @param value
     */
    @Deprecated
    public CustomPredicate(String field, Comparable value, Operation operation) {
        this(field, value, operation, false);
    }
    /**
     *  @deprecated  : Use CustomPredicate.procurePredicate(String s,Comparable c, Operation o, boolean b)
     *                Support will be deleted May 4th 2021.
     * @param field
     * @param value
     */
    @Deprecated
    public CustomPredicate(String field, Comparable value, Operation operation, boolean negate) {
        this.field = field;
        if(operation.equals(Operation.LIKE))
            this.value = "%"+value+"%";
          else
            this.value = value;


        this.operation = operation;
        this.negate = negate;
    }
    private static CustomPredicate buildCustomPredicate(String field,Comparable comparable, Operation operation,boolean negate,ConjunctionType conjunctionType){


            instance = new CustomPredicate();


        if(operation.equals(Operation.LIKE))
            comparable = "%"+comparable+"%";

            instance.setField(field);
            instance.setValue(comparable);
            instance.setOperation(operation);
            instance.setNegate(negate);
            if(conjunctionType != null)
              instance.setConjunctionType(conjunctionType);

        return instance;
    }
    private void setField(String field) {
        this.field = field;
    }

    private void setValue(Comparable value) {
        this.value = value;
    }

    private void setOtherValues(List<Comparable> otherValues) {
        this.otherValues = otherValues;
    }

    private void setOperation(Operation operation) {
        this.operation = operation;
    }

    private void setNegate(boolean negate) {
        this.negate = negate;
    }


}
