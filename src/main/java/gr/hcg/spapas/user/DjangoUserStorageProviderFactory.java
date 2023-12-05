package gr.hcg.spapas.user;

import com.zaxxer.hikari.HikariDataSource;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Serafeim Papastefanos
 */
public class DjangoUserStorageProviderFactory implements UserStorageProviderFactory<DjangoUserStorageProvider> {
    private static final Logger logger = Logger.getLogger(DjangoUserStorageProviderFactory.class);

    private Map<String, ProviderConfig> dataSourcePerInstance = new HashMap<>();

    private static class ProviderConfig {
        private HikariDataSource ds = null;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public DjangoUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        System.err.println("Create");
        ProviderConfig pc = dataSourcePerInstance.computeIfAbsent(model.getId(),
                s -> {
                    System.err.println("Create 2");
                    HikariDataSource ds = new HikariDataSource();
                    String url = "jdbc:mysql://user:user@127.0.0.1:3306/db";
                    System.err.println(0);
                    try {
                        System.err.println(1);
                        System.err.println(model.get("jdbcUrlEnv"));
                        url = System.getenv(model.get("jdbcUrlEnv"));
                        System.err.println(url);
                        System.err.println(2);
                        if (url == null || url.isEmpty()) {
                            System.err.println(3);
                            logger.error("JDBC URL not found!");
                        }
                    } catch (Exception e) {
                        System.err.println(4);
                        e.printStackTrace();
                    }
                    System.err.println(5);
                    ds.setJdbcUrl(url);
                    ProviderConfig dsConfig = new ProviderConfig();
                    dsConfig.ds = ds;
                    return dsConfig;
                }
        );
        return new DjangoUserStorageProvider(session, model, pc.ds);

    }

    @Override
    public String getId() {
        return "django-user-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property("jdbcUrlEnv", "Environment var with JDBC URL", "Please enter the name of an env var that contains the JDBC URL of the connection", ProviderConfigProperty.STRING_TYPE, "REGUSR_JDBC_URL", null)
                .build();
    }
}
