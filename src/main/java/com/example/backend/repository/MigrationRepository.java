package com.example.backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class MigrationRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     *
     */
    private static final String SQL_UPDATE = """
        UPDATE ASYNC_TEST
           SET SALE_AMT = AMT,
               PROMOTION_AMT = 0
         WHERE ROWID IN (:pkList)
    """;

    public void bulkUpdateByPkList(List<String> pkList) {
        if (pkList == null || pkList.isEmpty()) return;
        var query = em.createNativeQuery(SQL_UPDATE);
        query.setParameter("pkList", pkList);
        int updated = query.executeUpdate();
        log.debug("업데이트 완료: {}건", updated);
    }

//    public void bulkUpdateByPkList(List<String> pkList) {

//        if (pkList == null || pkList.isEmpty()) return;
//
//        String sql = """
//            UPDATE ASYNC_TEST
//               SET SALE_AMT = AMT,
//                   PROMOTION_AMT = 0
//             WHERE ROWID IN (:pkList)
//            """;
//
//        int updated = em.createNativeQuery(sql)
//                .setParameter("pkList", pkList)
//                .executeUpdate();
//
//        log.debug("업데이트 완료: {}건", updated);
//    }
}