package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service("msgService")
@Repository
@Transactional(readOnly = true)
public  class MessageService {

    @Autowired
    private GenericService genericService;
    @Autowired
    private SessionFactory sessionFactory;

    public List<User> loadUsers(boolean execOnly, Long pLoggedInId){

        List wRetList = new ArrayList();
        int filter = 0;
        if(execOnly)
            filter = 1;
        String wHqlStr = "select u.id, u.firstName, u.lastName, u.email from User u, BusinessClient b, Role r" +
                " where u.role.id = r.id and r.businessClient.id = b.id and b.execInd = "+filter+"" +
                " and u.id != "+pLoggedInId;

        Query wQuery = this.genericService.getCurrentSession().createQuery(wHqlStr);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                User u  = new User();
                u.setId((Long) o[0]);
                u.setFirstName((String) o[1]);
                u.setLastName((String) o[2]);
                u.setEmail((String) o[3]);
                wRetList.add(u);
            }
        }

        return wRetList;
    }

    @Transactional()
    public int updateMessageStatus(int uId, Long mId) {
        String hqlQuery = "update MessageObject set dataStatus= :uId where id = :mId";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("uId", uId);
        query.setParameter("mId", mId);

        int update = query.executeUpdate();

        return  update;
    }
}
