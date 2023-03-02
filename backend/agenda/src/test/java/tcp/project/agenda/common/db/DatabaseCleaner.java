package tcp.project.agenda.common.db;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class DatabaseCleaner implements InitializingBean {

    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE %s";
    private static final String RESET_TABLE_ID = "ALTER TABLE %s ALTER COLUMN ID RESTART WITH 1";
    public static final String REFERENTIAL = "SET REFERENTIAL_INTEGRITY %s";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource dataSource;

    private final List<String> tableNames = new ArrayList<>();

    @Transactional
    public void clear() {
        em.clear();
        query(String.format(REFERENTIAL, "FALSE"));
        for (String tableName : tableNames) {
            query(String.format(TRUNCATE_TABLE, tableName));
            query(String.format(RESET_TABLE_ID, tableName));
        }
        query(String.format(REFERENTIAL, "TRUE"));
    }

    private void query(String query) {
        em.createNativeQuery(query).executeUpdate();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            ResultSet tables = metaData.getTables(null, "PUBLIC", null, new String[]{"TABLE"});
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("테이블 이름 갱신 실패");
        }
    }
}
