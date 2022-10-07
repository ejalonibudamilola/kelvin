package com.osm.gnl.ippms.ogsg.auth.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.PasswordResetBean;
import com.osm.gnl.ippms.ogsg.auth.domain.Role;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.auth.repository.IUserRepository;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service("userRepo")
@Repository
@Transactional(readOnly = true)
public class UserRepository implements IUserRepository {
    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User findByUsername(String pUserName) {
        User user = genericService.getSingleObjectFromBuilder(
                new PredicateBuilder().addPredicate(
                        CustomPredicate.procurePredicate("username", pUserName, Operation.STRING_EQUALS)), User.class);
        return user;
    }

    @Override
    public List<User> loadActiveLoginUsingFilters(BusinessCertificate businessCertificate) {
        List<User> wRetList = new ArrayList<>();

        String hqlQuery = "select u.id,u.username,u.firstName,u.lastName, u.email,u.deactivatedInd, u.accountLocked, u.lastModTs, u.creationDate,u.createdBy.id, " +
                "u.createdBy.firstName, u.createdBy.lastName, u.lastLoginDate,r.id, r.name from User u, Role r where u.role.id = r.id and u.deactivatedInd = 0 and " +
                "u.accountLocked = 0 and r.businessClient.id = :pBizIdVar";

        if(!businessCertificate.isSuperAdmin())
            hqlQuery += " and r.adminAccessInd = 0";
        if(!businessCertificate.isSysUser())
            hqlQuery += " and u.systemUserInd = 0";

        Query<Object[]> query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());

        List<Object[]> wRetVal = query.list();

        if (wRetVal.size() > 0)
        {
            int i = 0;
            for (Object[] o : wRetVal) {

                User user = new User((Long)o[i++] , (String) o[i++], (String) o[i++], (String) o[i++]);
                user.setEmail((String)o[i++]);
                user.setDeactivatedInd((Integer)o[i++]);
                user.setAccountLocked((Integer)o[i++]);
                user.setLastModTs((Timestamp)o[i++]);
                user.setCreationDate((Timestamp)o[i++]);
                user.setCreatedBy(new User((Long)o[i++] , (String) o[i++], (String) o[i++]));
                user.setLastLoginDate((Timestamp)o[i++]);
                Role role = new Role((Long)o[i++] , (String) o[i++]);
                user.setRole(role);
                wRetList.add(user);
                i = 0;

            }

        }

        return wRetList;
    }

    @Override
    public List<User> loadLockedAccount(BusinessCertificate businessCertificate) {
        List<User> wRetList = new ArrayList<>();

        String hqlQuery = "select u.id,u.username,u.firstName,u.lastName, u.email,u.deactivatedInd, u.accountLocked, u.lastModTs, u.creationDate,u.createdBy.id, " +
                "u.createdBy.firstName, u.createdBy.lastName, r.id, r.name from User u, Role r where u.role.id = r.id and " +
                "u.accountLocked = 1 and r.businessClient.id = :pBizIdVar";

        if(!businessCertificate.isSuperAdmin())
            hqlQuery += " and r.adminAccessInd = 0";
        if(!businessCertificate.isSysUser())
            hqlQuery += " and u.systemUserInd = 0";

        Query<Object[]> query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId());

        List<Object[]> wRetVal = query.list();

        if (wRetVal.size() > 0)
        {
            int i = 0;
            for (Object[] o : wRetVal) {

                User user = new User((Long)o[i++] , (String) o[i++], (String) o[i++], (String) o[i++]);
                user.setEmail((String)o[i++]);
                user.setDeactivatedInd((Integer)o[i++]);
                user.setAccountLocked((Integer)o[i++]);
                user.setLastModTs((Timestamp)o[i++]);
                user.setCreationDate((Timestamp)o[i++]);
                user.setCreatedBy(new User((Long)o[i++] , (String) o[i++], (String) o[i++]));
                Role role = new Role((Long)o[i++] , (String) o[i++]);
                user.setRole(role);
                wRetList.add(user);
                i = 0;

            }

        }

        return wRetList;
    }

    @Override
    public PasswordResetBean loadPasswordResetBean(Long id) throws IllegalAccessException, InstantiationException {
        return genericService.loadObjectById(PasswordResetBean.class, id);
    }
}
