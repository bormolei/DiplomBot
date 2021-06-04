package DAO;

import model.MainModel;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.Actions;
import utils.HibernateSessionFactoryUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class HibernateController {
    public static void doHibernateAction(MainModel mainModel, Actions action) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx1 = session.beginTransaction();
            switchByActions(mainModel, action, session);
            tx1.commit();
        }
    }

    private static void switchByActions(MainModel mainModel, Actions action, Session session) {
        switch (action) {
            case SAVE:
            case UPDATE:
                session.saveOrUpdate(mainModel);
                break;
            case DELETE:
                session.delete(mainModel);
                break;
        }
    }

    public static List getAllRows(MainModel mainModel) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaQuery criteria = session.getCriteriaBuilder().createQuery(mainModel.getClass());
            criteria.from(mainModel.getClass());
            return session.createQuery(criteria).getResultList();
        }
    }

    public static List<? extends MainModel> getColumnByField(MainModel mainModel, String field) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<? extends MainModel> criteria = builder.createQuery(mainModel.getClass());
            Root<? extends MainModel> root = criteria.from(mainModel.getClass());
            criteria.select(root.get(field));
            criteria.from(mainModel.getClass());
            return session.createQuery(criteria).getResultList();
        }
    }

    public static List<? extends MainModel> getRowsByField(MainModel mainModel, String fieldName, Integer condition) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<? extends MainModel> criteria = builder.createQuery(mainModel.getClass());
            Root root = criteria.from(mainModel.getClass());
            criteria.select(root).where(builder.equal(root.get(fieldName), condition));
            return session.createQuery(criteria).getResultList();
        }
    }

    public static List<? extends MainModel> getRowsByField(MainModel mainModel, String fieldName, String condition) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<? extends MainModel> criteria = builder.createQuery(mainModel.getClass());
            Root root = criteria.from(mainModel.getClass());
            criteria.select(root).where(builder.equal(root.get(fieldName), condition));
            return session.createQuery(criteria).getResultList();
        }
    }

    public static List<? extends MainModel> getUserRows(MainModel mainModel, String fieldName, Long condition) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<? extends MainModel> criteria = builder.createQuery(mainModel.getClass());
            Root root = criteria.from(mainModel.getClass());
            criteria.select(root).where(builder.equal(root.get(fieldName), condition));
            return session.createQuery(criteria).getResultList();
        }
    }
}
