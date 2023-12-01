package gr.hcg.spapas.user;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Created by serafeim on 17/3/2016.
 */
public class DjangoPasswordHasher {
    SecretKeyFactory skf;

    public DjangoPasswordHasher() throws NoSuchAlgorithmException {
        skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    }

    public boolean checkDjangoPassword(String encoded, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String[] parts = encoded.split("\\$");
        if(parts.length<4) {
            return false;
        }
        String algorithm = parts[0];
        int iterations = Integer.valueOf(parts[1]);
        String salt = parts[2];
        String hash = parts[3];

        String generated = generateDjangoPasswordHash(password, iterations, salt);

        return (generated.equals(hash));
    }

    private String generateDjangoPasswordHash(String password, int iterations, String saltStr) throws NoSuchAlgorithmException, InvalidKeySpecException {

        char[] chars = password.toCharArray();
        byte[] salt = saltStr.getBytes(Charset.forName("UTF-8"));

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 256 );

        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
}