package de.entropia.logistiktracking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void cleanDatabase() {
        em.createQuery("delete from UserDatabaseElement ").executeUpdate();
        em.createQuery("DELETE FROM EuroCrateDatabaseElement").executeUpdate();
        em.createQuery("DELETE FROM PackingListDatabaseElement").executeUpdate();
    }

    public <T> T saveNew(T t) {
        T v = em.merge(t);
        em.persist(v);
        return v;
    }

    public void update(Object t) {
        em.persist(t);
    }

    @SuppressWarnings("unchecked")
	public <T> T find(Object id, T... typeProbe) {
        T t = (T) em.find(typeProbe.getClass().componentType(), id);
        em.refresh(t);
        return t;
    }

    public void flush() {
        em.flush();
    }
}
