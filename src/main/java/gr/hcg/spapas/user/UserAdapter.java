package gr.hcg.spapas.user;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserAdapter extends AbstractUserAdapter {

    private final DjangoUser user;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, DjangoUser user) {
        super(session, realm, model);
        this.storageId = new StorageId(storageProviderModel.getId(), user.id);
        this.user = user;
    }

    @Override
    public String getFirstName() {
        return user.firstName;
    }

    @Override
    public String getLastName() {
        return user.lastName;
    }

    @Override
    public String getEmail() {
        return user.email;
    }

    @Override
    public String getUsername() { return user.username; }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new LegacyUserCredentialManager(session, realm, this);
    }

    @Override
    public Long getCreatedTimestamp() {
        return user.created;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL, getEmail());
        attributes.add(UserModel.FIRST_NAME, getFirstName());
        attributes.add(UserModel.LAST_NAME, getLastName());

        attributes.add("fatherName", user.fatherName);
        attributes.add("motherName", user.motherName);
        attributes.add("dob", user.dob);
        attributes.add("lastLogin", user.lastLogin);

        return attributes;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        if (name.equals(UserModel.USERNAME) || name.equals(UserModel.EMAIL)) {
            return Stream.of(getUsername());
        }
        return Stream.empty();
    }

    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        if (user.roles != null) {
            return user.roles.stream().map(roleName -> new UserRoleModel(roleName, realm)).collect(Collectors.toSet());
        }
        return Set.of();
    }
}
