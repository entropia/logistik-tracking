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
        em.createQuery("DELETE FROM PackingListDatabaseElement").executeUpdate();
        em.createQuery("DELETE FROM EuroPalletDatabaseElement").executeUpdate();
        em.createQuery("DELETE FROM EuroCrateDatabaseElement ").executeUpdate();
        em.createQuery("DELETE FROM LocationDatabaseElement ").executeUpdate();
    }
}
