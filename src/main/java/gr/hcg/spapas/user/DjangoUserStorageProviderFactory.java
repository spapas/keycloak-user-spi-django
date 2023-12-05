package gr.hcg.spapas.user;

import com.zaxxer.hikari.HikariDataSource;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

/**
 * @author Serafeim Papastefanos
 */
public class DjangoUserStorageProviderFactory implements UserStorageProviderFactory<DjangoUserStorageProvider> {
    private static final Logger logger = Logger.getLogger(DjangoUserStorageProviderFactory.class);
    @Override
    public DjangoUserStorageProvider create(KeycloakSession session, ComponentModel model) {

        HikariDataSource ds = new HikariDataSource();
        String url = System.getenv(model.get("jdbcUrlEnv"));
        if(url == null  || url.isEmpty()) {
            logger.error("JDBC URL not found!");
            url = "jdbc:mysql://user:user@127.0.0.1:3306/db";
        }
        ds.setJdbcUrl(url);

        return new DjangoUserStorageProvider(session, model, ds);
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
