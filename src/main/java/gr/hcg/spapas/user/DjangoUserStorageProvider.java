package gr.hcg.spapas.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.*;
import org.keycloak.models.GroupModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.stream.Stream;

import org.jboss.logging.Logger;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import javax.sql.DataSource;

public class DjangoUserStorageProvider
        implements UserStorageProvider, UserLookupProvider, CredentialInputValidator, CredentialInputUpdater, UserQueryProvider, UserRegistrationProvider {

    protected KeycloakSession session;
    protected DjangoRepository dr;
    protected ComponentModel config;
    protected DjangoPasswordHasher hasher;


    private static final Logger logger = Logger.getLogger(DjangoUserStorageProvider.class);

    public DjangoUserStorageProvider(KeycloakSession session, ComponentModel config, DataSource ds) {
        this.session = session;
        this.config = config;
        this.dr = new DjangoRepository(ds);
        try {
            this.hasher = new DjangoPasswordHasher();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        logger.info("Initializing the DjangoUserStorageProvider, ds = " + ds+", users count = " + dr.getUsersCount());
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        logger.info("getUserByUsername " + username);
        DjangoUser user = dr.findUserById(username);
        UserModel adapter = null;
        if(user!=null && user.password!=null) {
            adapter = createAdapter(realm, user);
        }
        return adapter;
    }

    protected UserModel createAdapter(RealmModel realm, DjangoUser user) {
        return new UserAdapter(session, realm, config, user);
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        logger.info("getUserById " + id);
        //StorageId storageId = new StorageId(id);
        //String username = storageId.getExternalId();
        //return getUserByUsername(realm, username);
        String externalId = StorageId.externalId(id);
        logger.info("externalID " + externalId);
        DjangoUser user = dr.findUserById(externalId);
        logger.info("externalID user" + user);
        return new UserAdapter(session, realm, config, user);


    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        logger.info("getUserByEmail " + email);
        return getUserById(realm, email);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel um, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        //return credentialType.equals(CredentialModel.PASSWORD);
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel um, CredentialInput input) {
        logger.info("Is valid starting, um " + um +"/" + um.getUsername());
        DjangoUser du = dr.findUserById(um.getUsername());
        String rawpwd = input.getChallengeResponse();

        try {
            return du!=null && du.password !=null && hasher.checkDjangoPassword(du.password, rawpwd);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (input.getType().equals(CredentialModel.PASSWORD))
            throw new ReadOnlyException("user is read only for this update");

        return false;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realmModel, UserModel userModel) {
        return Stream.empty();
    }


    @Override
    public void close() {

    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> map, Integer integer, Integer integer1) {
        logger.info("Searcing for users: " + map);
        return dr.findUsers(map.get(UserModel.SEARCH )).stream().map(u -> createAdapter(realm, u));

    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realmModel, GroupModel groupModel, Integer integer, Integer integer1) {
        return null;
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realmModel, String s, String s1) {
        return null;
    }

    @Override
    public UserModel addUser(RealmModel realmModel, String s) {
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        return false;
    }
}
