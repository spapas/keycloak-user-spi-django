package org.keycloak.credential;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class LegacyUserCredentialManager extends UserCredentialManager {

    public LegacyUserCredentialManager(KeycloakSession session, RealmModel realm, UserModel user) {
        super(session, realm, user);
    }
}