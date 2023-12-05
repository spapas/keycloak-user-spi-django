package gr.hcg.spapas.user;

import com.zaxxer.hikari.HikariDataSource;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Serafeim Papastefanos
 */
public class DjangoUserStorageProviderFactory implements UserStorageProviderFactory<DjangoUserStorageProvider> {
    private static final Logger logger = Logger.getLogger(DjangoUserStorageProviderFactory.class);
    private Map<String, DataSource> dataSourcePerInstance = new HashMap<>();

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public DjangoUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        DataSource ds = dataSourcePerInstance.computeIfAbsent(model.getId(),
                s -> {
                    HikariDataSource newds = new HikariDataSource();
                    String url = "jdbc:mysql://user:user@127.0.0.1:3306/db";
                    try {
                        url = System.getenv(model.get("jdbcUrlEnv"));
                        if (url == null || url.isEmpty()) {

                            logger.error("JDBC URL not found!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    newds.setJdbcUrl(url);
                    return newds;
                }
        );
        return new DjangoUserStorageProvider(session, model, ds);

    }

    @Override
    public String getId() {
        return "django-user-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property()
                .name("jdbcUrlEnv")
                .label("Environment var with JDBC URL")
                .helpText("Please enter the name of an env var that contains the JDBC URL of the connection")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("REGUSR_JDBC_URL")
                .add()
                .build();
    }
}
