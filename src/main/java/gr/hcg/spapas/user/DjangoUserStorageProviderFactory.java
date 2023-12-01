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
        String url = System.getenv("REGUSR_JDBC_URL");
        if(url == null  || url.isEmpty()) {
            logger.error("REGUSR_JDBC_URL Not found!");
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
                .property("myParam", "My Param", "Some Description", ProviderConfigProperty.STRING_TYPE, "some value", null)
                .build();
    }
}
