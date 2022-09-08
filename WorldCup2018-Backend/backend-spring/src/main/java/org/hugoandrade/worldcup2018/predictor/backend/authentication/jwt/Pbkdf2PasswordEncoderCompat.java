package org.hugoandrade.worldcup2018.predictor.backend.authentication.jwt;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

public class Pbkdf2PasswordEncoderCompat extends Pbkdf2PasswordEncoder {

    private final int iterations;
    private final int hashWidth;
    private final BytesKeyGenerator saltGenerator;

    public Pbkdf2PasswordEncoderCompat(int iterations, int hashWidth) {
        super("", iterations, hashWidth);

        this.iterations = iterations;
        this.hashWidth = hashWidth;
        this.saltGenerator = KeyGenerators.secureRandom(hashWidth);

        setEncodeHashAsBase64(true);
        setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1);
    }


    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = this.saltGenerator.generateKey();
        byte[] encoded = this.encode(rawPassword, salt);
        return this.encode(salt) + this.encode(encoded);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // password includes salt
        byte[] salt = decode(encodedPassword.substring(0, encodedPassword.length() / 2));
        encodedPassword = encodedPassword.substring(encodedPassword.length() / 2);
        byte[] digested = this.decode(encodedPassword);
        return MessageDigest.isEqual(digested, encode(rawPassword, salt));
    }

    private byte[] encode(CharSequence rawPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(), EncodingUtils.concatenate(new byte[][]{salt}), iterations, hashWidth * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1.name());
            return EncodingUtils.concatenate(new byte[][]{skf.generateSecret(spec).getEncoded()});
        } catch (GeneralSecurityException var5) {
            throw new IllegalStateException("Could not create hash", var5);
        }
    }

    private byte[] decode(String encodedBytes) {
        return Base64.getDecoder().decode(encodedBytes);
    }

    private String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
