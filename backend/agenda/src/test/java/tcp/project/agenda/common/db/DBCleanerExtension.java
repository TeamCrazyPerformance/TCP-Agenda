package tcp.project.agenda.common.db;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DBCleanerExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        DatabaseCleaner databaseCleaner = (DatabaseCleaner) SpringExtension.getApplicationContext(context)
                .getBean("databaseCleaner");
        databaseCleaner.clear();
    }
}
