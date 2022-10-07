/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.repository;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.OrderBy;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.dao.IGenericDao;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class GenericDaoImpl  implements IGenericDao {

    protected SessionFactory sessionFactory;
    protected IppmsUtils utils;



    @Autowired
    public GenericDaoImpl(final SessionFactory sessionFactory, final IppmsUtils utils) {
        this.sessionFactory = sessionFactory;
        this.utils = utils;
    }

    public GenericDaoImpl() {

    }


    @Override
    public <T> List<T> loadAllObjectsUsingRestrictions(Class<T> pObjectClass, final List<CustomPredicate> predicates, String order) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);
        Root<T> root = query.from(pObjectClass);
        if(IppmsUtils.isNotNullOrEmpty(order) ) {
            query.orderBy(builder.asc(root.get(order))); //Assuming 'order' is on the root object

        }
        TypedQuery<T> typedQuery = getTypedQueryFromPredicates(builder, query, root, predicates);

        List<T> retList = typedQuery.getResultList();

        if(retList == null)
            retList = new ArrayList<>();

        return retList;
    }

    @Override
    public Long getTotalPaginatedObjects(Class<?> clazz, List<CustomPredicate> predicates) {

        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        //query.select(builder.countDistinct(query.from(clazz)));
        Root<?> root = query.from(clazz);
        query.select(PredicateBuilder.getPath("id", root, Long.class));
        TypedQuery<Long> typedQuery = getTypedQueryFromPredicatesForCount(builder, query, root, clazz, predicates);

        int wRetVal = typedQuery.getResultList().size();

        return new Long(wRetVal);
    }
    @Override
    public <T> int countObjectsUsingPredicateBuilder(PredicateBuilder predicateBuilder, Class<T> clazz) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(clazz);

        //Predicate where = builder.conjunction();
        //where = predicateBuilder.build(builder, root, where);
        //query.where(where);
        query.select(PredicateBuilder.getPath("id", root, Long.class));

        //TypedQuery<Long> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
        TypedQuery<Long> typedQuery = getTypedQueryFromPredicatesForCount(builder,query,root,clazz,predicateBuilder.getPredicates());
        return typedQuery.getResultList().size();
    }

    private  TypedQuery<Long> getTypedQueryFromPredicatesForCount(CriteriaBuilder builder, CriteriaQuery<Long> query,
                                                                  Root<?> root, Class<?> pObjectClass, List<CustomPredicate> predicates) {
        Predicate where = builder.conjunction();
        for(CustomPredicate predicate : predicates){
            where = GenericDaoImplHelper.preparePredicate(where,builder,predicate,root);
        }
        query.where(where);
        TypedQuery<Long> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);

        return typedQuery;
    }
    private <T> TypedQuery<T> getTypedQueryFromPredicates(CriteriaBuilder builder, CriteriaQuery<T> query,
                                                          Root<T> root, List<CustomPredicate> predicates) {
        query.select(root);
        if(!predicates.isEmpty()) {
            Predicate where = builder.conjunction();

            for (CustomPredicate predicate : predicates) {
                // where = builder.and(where, builder.equal(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
                where = GenericDaoImplHelper.preparePredicate(where,builder,predicate,root);
            }
            query.where(where);
        }
        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
        return typedQuery;
    }
    @Override
    public <T> T loadObjectUsingRestriction(Class<T> pObjectClass, List<CustomPredicate> predicates) throws IllegalAccessException, InstantiationException {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);
        Root<T> root = query.from(pObjectClass);
        TypedQuery<T> typedQuery = getTypedQueryFromPredicates(builder, query, root, predicates);
        T classInstance = null;
        try {
            classInstance = typedQuery.getSingleResult();
        }
        catch(Exception e){
            //e.printStackTrace();
        }
        if (classInstance == null)
            classInstance = pObjectClass.newInstance();
        return classInstance;
    }

    @Override
    public <T> T loadObjectUsingRestrictionAllowNull(Class<T> pObjectClass, List<CustomPredicate> predicates) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);
        Root<T> root = query.from(pObjectClass);
        TypedQuery<T> typedQuery = getTypedQueryFromPredicates(builder, query, root, predicates);

        T classInstance = null;
        try {
            classInstance = typedQuery.getSingleResult();
        }
        catch(Exception e){
            //e.printStackTrace();
        }
        return classInstance;


    }


    @Override
    public <T> List<T> loadPaginatedObjectsByPredicates(Class<T> clazz, PredicateBuilder predicateBuilder, int pStartRowNum, int pEndRowNum, String pSortOrder, String pSortCriterion) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        Predicate where = builder.conjunction();
        where = predicateBuilder.build(builder, root, where);
        query.where(where);
        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
        typedQuery.setFirstResult(pStartRowNum).setMaxResults(pEndRowNum);
        query.orderBy(builder.asc(root.get("id")));
        return typedQuery.getResultList();
    }


    @Override
    public <T> List<T> loadPaginatedObjects(Class<T> pObjectClass, List<CustomPredicate> predicates, int pStartRowNum, int pEndRowNum,
                                            String pSortOrder, String pSortCriterion) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);

        Root<T> root = query.from(pObjectClass);
        TypedQuery<T> typedQuery = getTypedQueryFromPredicates(builder, query, root, predicates);
        typedQuery.setFirstResult(pStartRowNum).setMaxResults(pEndRowNum);
        query.orderBy(builder.asc(root.get("id")));

        return typedQuery.getResultList();
    }




    @Override
    @Transactional
    public  void storeObject(Object pObject) {

        this.sessionFactory.getCurrentSession().saveOrUpdate(pObject);
    }

    @Override
    public <T> List<T> loadObjectsUsingPredicateBuilder(PredicateBuilder predicateBuilder, Class<T> clazz) {
        TypedQuery<T> typedQuery = getTypedQueryFromBuilder(predicateBuilder, clazz, Collections.EMPTY_LIST);
        return typedQuery.getResultList();
    }

    @Override
    public <T, X extends Number> X sumFieldUsingPredicateBuilder(Class<T> rootClass, PredicateBuilder predicateBuilder, Class<X> sumClass, String sumField,
                                                                 List<String> groupByFields) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<X> query = builder.createQuery(sumClass);
        Root<T> root = query.from(rootClass);
        Predicate where = builder.conjunction();
        where = predicateBuilder.build(builder, root, where);
        query.where(where);
        if (!groupByFields.isEmpty()) {
            List<Expression<?>> expressions = new ArrayList<>();
            for (String field: groupByFields)
                expressions.add(PredicateBuilder.getPath(field, root));
            query.groupBy(expressions);
        }
        query.select(builder.sum(PredicateBuilder.getPath(sumField, root, sumClass)));
        TypedQuery<X> typedQuery;
        X result;
        try {
            typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
             result = typedQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return result;
    }


    @Override
    public <T> List<T> loadAllObjectsWithoutRestrictions(Class<T> pObjectClass, String order) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);
        Root<T> root = query.from(pObjectClass);
        query.select(root);
        if(IppmsUtils.isNotNullOrEmpty(order) )
            query.orderBy(builder.asc(root.get(order))); //Assuming 'order' is on the root object

        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);

        return typedQuery.getResultList();
    }

    @Override
    public <T> List<T> loadObjectsUsingPredicateBuilder(PredicateBuilder predicateBuilder, Class<T> clazz, List<OrderBy> orderBy) {
        TypedQuery<T> typedQuery = getTypedQueryFromBuilder(predicateBuilder, clazz, orderBy);
        return typedQuery.getResultList();
    }

    @Override
    public <T> T loadSingleObjectUsingPredicateBuilder(PredicateBuilder predicateBuilder, Class<T> clazz) {
        TypedQuery<T> typedQuery = getTypedQueryFromBuilder(predicateBuilder, clazz, Collections.EMPTY_LIST);

        return typedQuery.getSingleResult();
    }

    public <T> TypedQuery<T> getTypedQueryFromBuilder(PredicateBuilder predicateBuilder, Class<T> clazz, List<OrderBy> orderBy) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        Predicate where = builder.conjunction();
        where = predicateBuilder.build(builder, root, where);
        query.where(where);
        if (!orderBy.isEmpty()) {
            List<javax.persistence.criteria.Order> orders = new ArrayList<>();
            for (OrderBy order: orderBy) {
                orders.add(order.isAsc() ? new OrderImpl(PredicateBuilder.getPath(order.getField(), root)) :
                        new OrderImpl(PredicateBuilder.getPath(order.getField(), root), false));
            }
            query.orderBy(orders);
        }

        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
        return typedQuery;
    }

    @Override
    public <T> List<T> loadAllObjectsWithSingleCondition(Class<T> pObjectClass, CustomPredicate customPredicate, String order) {
        List<CustomPredicate> wList = new ArrayList<CustomPredicate>();
        wList.add(customPredicate);
        return this.loadAllObjectsUsingRestrictions(pObjectClass,wList,order);
    }


    public <T> T loadObjectWithSingleCondition(Class<T> pObjectClass, CustomPredicate customPredicate) throws InstantiationException, IllegalAccessException {
        List<CustomPredicate> wList = new ArrayList<CustomPredicate>();
        wList.add(customPredicate);
        return this.loadObjectUsingRestriction(pObjectClass,wList);
    }

    @Override
    public <T> T loadObjectWithSingleConditionAllowNull(Class<T> pObjectClass, CustomPredicate customPredicate) throws InstantiationException, IllegalAccessException {
        List<CustomPredicate> wList = new ArrayList<CustomPredicate>();
        wList.add(customPredicate);
        return this.loadObjectUsingRestrictionAllowNull(pObjectClass,wList);
    }

    @Override
    public <T> T loadObjectById(Class<T> pObjectClass, Long pId)throws IllegalAccessException, InstantiationException {

        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);
        Root<T> root = query.from(pObjectClass);
        ParameterExpression<Long> parameter = builder.parameter(Long.class);
        query.select(root).where(builder.equal(root.get("id"),parameter));
        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
        typedQuery.setParameter(parameter,pId);
        T classInstance;
        try{
            classInstance = typedQuery.getSingleResult();
            if(classInstance == null)
                classInstance = pObjectClass.newInstance();
        }catch(Exception ex){
            classInstance = pObjectClass.newInstance();
        }

        return classInstance;
    }

    @Override
    public <T> List<T> loadControlEntity(Class<T> clazz) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        Field[] wFields = clazz.getDeclaredFields();

        for(Field m : wFields) {
            if(m.getName().equalsIgnoreCase("name")) {
                query.orderBy(new OrderImpl(PredicateBuilder.getPath(m.getName(), root)));
                break;
            }
        }
        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
        return typedQuery.getResultList();
    }

    @Override
    @Transactional()
    public Long saveObject(Object object) {
        object = this.sessionFactory.getCurrentSession().merge(object);
        return (Long) this.sessionFactory.getCurrentSession().save(object);
    }

    @Override
    @Transactional()
    public void storeObjectBatch(List<?> pFSaveList) {
        Session session = this.sessionFactory.getCurrentSession();
        if(session.isDirty())
            session.flush();

        for (int i = 0; i < pFSaveList.size(); i++)
        {
            session.saveOrUpdate(pFSaveList.get(i));
            if ((i == 49)) {
                session.flush();
                session.clear();
            }

        }
        session.flush();
        session.clear();
    }

    @Override
    @Transactional
    public void storeVectorObjectBatch(Vector<?> pFSaveList) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        if(session.isDirty())
            session.flush();

        for (int i = 0; i < pFSaveList.size(); i++)
        {
             session.saveOrUpdate(pFSaveList.get(i));
            if ((i % 20 != 0) && (i != pFSaveList.size() - 1)) {
                continue;
            }
            session.flush();
            session.clear();
        }

        tx.commit();
        session.close();
    }


    @Override
    @Transactional()
    public void deleteObject(Object object) {
        this.sessionFactory.getCurrentSession().delete(object);
        this.sessionFactory.getCurrentSession().flush();
    }

    @Override
    public <T> boolean isObjectExisting(Class<T> clazz, PredicateBuilder predicateBuilder) {
        TypedQuery<T> typedQuery = getTypedQueryFromBuilder(predicateBuilder, clazz, Collections.EMPTY_LIST);
        if(typedQuery.getResultList() == null)
            return false;
        return !typedQuery.getResultList().isEmpty();
    }

    @Override
    public Session getCurrentSession()   {

        if(sessionFactory.getCurrentSession() == null)
            sessionFactory.openSession();

        return this.sessionFactory.getCurrentSession();
    }

    public Long loadMaxValueByClassAndLongColName(Class<?> clazz, String pLongColumnOrmName) {
        List results = this.sessionFactory
                .getCurrentSession()
                .createCriteria(clazz)
                .setProjection(Projections.max(pLongColumnOrmName)).list();

        if ((results == null) || (results.size() < 1) || (results.isEmpty()) || (results.get(0) == null)) {
            return 0L;
        }
        return ((Long)results.get(0));
    }
    public Long loadMaxValueByClassClientIdAndColumn(Class<?> clazz, String pLongColumnOrmName, Long pBizClientId, String pBizClientFieldName) {
        List results = this.sessionFactory
                .getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(pBizClientFieldName,pBizClientId ))
                .setProjection(Projections.max(pLongColumnOrmName)).list();

        if ((results == null) || (results.size() < 1) || (results.isEmpty()) || (results.get(0) == null)) {
            return 0L;
        }
        return ((Long)results.get(0));
    }
    @Override
    public <T> HashMap<Long, T> loadObjectsAsMap(Class<T> pObjectClass, List<CustomPredicate> predicates, String pMethodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);
        Root<T> root = query.from(pObjectClass);
        query.select(root);
        Predicate where = builder.conjunction();
        for(CustomPredicate predicate : predicates){
            where = builder.and(where, builder.equal(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
        }
        query.where(where);
        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);

        List<T> classInstance = typedQuery.getResultList();

        return this.makeMap(classInstance, pMethodName);
    }
    @Override
    public <T> HashMap<Long, T> loadAllObjectsAsMapWithoutRestrictions(Class<T> pObjectClass, String pMethodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);
        Root<T> root = query.from(pObjectClass);
        query.select(root);

        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
        List<T> instances = typedQuery.getResultList();

        return this.makeMap(instances, pMethodName);
    }
    @Override
    public <T> HashMap<?, T> loadObjectsAsMapWithStringKey(Class<T> pObjectClass, List<CustomPredicate> predicates, String pMethodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(pObjectClass);
        Root<T> root = query.from(pObjectClass);
        query.select(root);
        Predicate where = builder.conjunction();
        for(CustomPredicate predicate : predicates){
            where = builder.and(where, builder.equal(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
        }
        query.where(where);
        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);

        List<T> classInstance = typedQuery.getResultList();

        return this.makeMapWithObjectKey(classInstance, pMethodName);
    }

    @Override
    public int getTotalNoOfModelObjectByClass(Class<?> pObjectClass,String pOrmCol ,boolean pDistinct) {
        CriteriaBuilder cb = this.sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<?> root = criteriaQuery.from(pObjectClass);

        if(pDistinct)
         criteriaQuery.select(cb.countDistinct(PredicateBuilder.getPath(pOrmCol, root)));
        else
            criteriaQuery.select(cb.count(PredicateBuilder.getPath(pOrmCol, root)));

        TypedQuery<Long> typedQuery = this.sessionFactory.getCurrentSession().createQuery(criteriaQuery);
        return  typedQuery.getSingleResult().intValue();

    }

    @Override
    @Transactional()
    public <T> void deleteObjectsWithConditions(Class<T> pObjectClass, List<CustomPredicate> predicates) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaDelete<T> query = builder.createCriteriaDelete(pObjectClass);
        Root<T>  root = query.from(pObjectClass);

        Predicate where = builder.conjunction();
        for(CustomPredicate predicate : predicates){
            where = builder.and(where, builder.equal(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
        }
        query.where(where);
        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);
        typedQuery.executeUpdate();
    }

    @Override
    public <T> T loadDefaultEntity(Class<T> clazz, List<CustomPredicate> predicates) throws IllegalAccessException, InstantiationException {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.select(root);
        Predicate where = builder.conjunction();
        for(CustomPredicate predicate : predicates){
            where = builder.and(where, builder.equal(PredicateBuilder.getPath(predicate, root), predicate.getValue()));
        }
        query.where(where);
        TypedQuery<T> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);

        T classInstance = typedQuery.getSingleResult();
        if(classInstance == null)
            classInstance = clazz.newInstance();
        return classInstance;

    }

    @Override
    public <T> int loadMaxValueByClassAndIntColName(Class<T> pClass, String colName) {
        List results = this.sessionFactory
                .getCurrentSession()
                .createCriteria(pClass)
                .setProjection(Projections.max(colName)).list();

        if ((results == null) || (results.size() < 1) || (results.isEmpty()) || (results.get(0) == null)) {
            return 0;
        }
        return (int) results.get(0);
    }

     @Override
    public List<NamedEntity> loadControlObjectsByIdAndName(BusinessCertificate bc, String pHqlStr, boolean pUseParent) {
        Query query = this.sessionFactory.getCurrentSession().createQuery(pHqlStr);
        if(pUseParent)
            query.setParameter("pBizIdVar", bc.getParentClientId());
        else
            query.setParameter("pBizIdVar", bc.getBusinessClientInstId());

        List<NamedEntity> wNamedEntity = new ArrayList<NamedEntity>();
        ArrayList<Object[]> wRetVal = (ArrayList)query.list();
        for (Object[] o : wRetVal){
            NamedEntity n = new NamedEntity();
            n.setId((Long)o[0]);
            n.setName((String)o[1]);

            wNamedEntity.add(n);

        }

        return wNamedEntity;

    }

    @Override
    public Long getTotalPaginatedObjectsByPredicate(Class<?> clazz, List<CustomPredicate> predicates) {
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        query.select(builder.countDistinct(query.from(clazz)));
        Root<?> root = query.from(clazz);
        TypedQuery<Long> typedQuery = getTypedQueryFromPredicatesForCount2(builder, query, root, clazz, predicates);

        return typedQuery.getSingleResult();
    }



    private TypedQuery<Long> getTypedQueryFromPredicatesForCount2(CriteriaBuilder builder, CriteriaQuery<Long> query, Root<?> root, Class<?> clazz, List<CustomPredicate> predicates) {
        Predicate where = builder.conjunction();
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        predicateBuilder.addPredicate(predicates);
        where = predicateBuilder.build(builder, root, where);
        query.where(where);
        TypedQuery<Long> typedQuery = this.sessionFactory.getCurrentSession().createQuery(query);

        return typedQuery;
    }


    private <T> HashMap<Long,T> makeMap(List<T> classInstance, String fieldName) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Object[] noparams = {};
        Class<?>[] parameterTypes = null;
        HashMap<Long,T> wMap = new HashMap<>();

        for(T t : classInstance){
            Method method = t.getClass().getDeclaredMethod("get"+camelCaseIt(fieldName), parameterTypes);
            Long id = (Long) method.invoke(t, noparams);
            wMap.put(id,t);
        }
        return wMap;
    }
    private <T> HashMap<?,T> makeMapWithObjectKey(List<T> classInstance, String methodName) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Object[] noparams = {};
        HashMap<Object,T> wMap = new HashMap<>();
        boolean wClassHasMethod =true;
        String childMethodName;
        String childMethodMethodName;
        if(IppmsUtils.isNullOrEmpty(classInstance)) return wMap;
        //first see if it is a compound method...ala employee.id for instance.
        if(methodName.indexOf(".") != -1 ){
            //Compound Type.
            childMethodName = methodName.substring(0,methodName.indexOf("."));
            try{
                classInstance.get(0).getClass().getMethod("get"+camelCaseIt(childMethodName), (Class<?>[]) null);
                //if we get here, now invoke and get the Object...

            }catch (NoSuchMethodException | SecurityException e){
                return wMap;
            }
             try{
                 Method method = classInstance.get(0).getClass().getDeclaredMethod("get"+camelCaseIt(childMethodName), (Class<?>[]) null);
                 Object embeddedClass = method.invoke(classInstance.get(0), noparams);
                 childMethodMethodName =  methodName.substring(methodName.indexOf(".") + 1);
                 embeddedClass.getClass().getMethod("get"+camelCaseIt(childMethodMethodName), (Class<?>[]) null);
                 Object key;
                 //if we get here....make the Map.
                 for(T t : classInstance){
                     method = t.getClass().getDeclaredMethod("get"+camelCaseIt(childMethodName), (Class<?>[]) null);
                     embeddedClass = method.invoke(t, noparams);
                     method = embeddedClass.getClass().getDeclaredMethod("get"+camelCaseIt(childMethodMethodName), (Class<?>[]) null);
                     key = method.invoke(embeddedClass, noparams);
                     wMap.put(key,t);
                 }
            }catch (NoSuchMethodException | SecurityException e){
                return wMap;
            }
        }else{
            try {
                classInstance.get(0).getClass().getMethod("get"+camelCaseIt(methodName), (Class<?>[]) null);
            } catch (NoSuchMethodException | SecurityException  e) {
                wClassHasMethod = false;
            }
            if(!wClassHasMethod)
                return wMap;
            for(T t : classInstance){
                Method method = t.getClass().getDeclaredMethod("get"+camelCaseIt(methodName), (Class<?>[]) null);
                Object key = method.invoke(t, noparams);
                wMap.put(key,t);
            }
        }


        return wMap;
    }

    private String camelCaseIt(String fieldName) {
        return String.valueOf(fieldName.charAt(0)).toUpperCase()+fieldName.substring(1);
    }

}
