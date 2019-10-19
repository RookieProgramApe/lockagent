package com.lxkj.utils;


import java.util.Random;

public class LianLianPaySecurity {
    public LianLianPaySecurity() {
    }

    public static String encrypt(String plaintext, String public_key) throws Exception {
        String hmack_key = genLetterDigitRandom(32);
        String version = "lianpay1_0_1";
        String aes_key = genLetterDigitRandom(32);
        String nonce = genLetterDigitRandom(8);
        return UberEncrypt.uberEncrypt(plaintext, public_key, hmack_key, version, aes_key, nonce);
    }

    public static boolean isvalidate(String ciphertext, String private_key) throws Exception {
        if (FuncUtils.isNull(ciphertext)) {
            return false;
        } else {
            String[] ciphertextArry = ciphertext.split("\\$");
            String base64_encrypted_hmac_key = ciphertextArry.length > 1 ? ciphertextArry[1] : "";
            String base64_nonce = ciphertextArry.length > 3 ? ciphertextArry[3] : "";
            String base64_ciphertext = ciphertextArry.length > 4 ? ciphertextArry[4] : "";
            String signature = ciphertextArry.length > 5 ? ciphertextArry[5] : "";
            return UberEncrypt.isvalidate(base64_nonce, base64_encrypted_hmac_key, private_key, base64_ciphertext, signature);
        }
    }

    public static String decrypt(String ciphertext, String private_key) throws Exception {
        if (FuncUtils.isNull(ciphertext)) {
            return "";
        } else {
            String[] ciphertextArry = ciphertext.split("\\$");
            String base64_encrypted_aes_key = ciphertextArry.length > 2 ? ciphertextArry[2] : "";
            String base64_nonce = ciphertextArry.length > 3 ? ciphertextArry[3] : "";
            String base64_ciphertext = ciphertextArry.length > 4 ? ciphertextArry[4] : "";
            return UberEncrypt.uberDecrypt(base64_ciphertext, base64_encrypted_aes_key, base64_nonce, private_key);
        }
    }

    public static String genLetterDigitRandom(int size) {
        StringBuffer allLetterDigit = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Random random = new Random();
        StringBuffer randomSb = new StringBuffer("");

        for(int i = 0; i < size; ++i) {
            randomSb.append(allLetterDigit.charAt(random.nextInt(allLetterDigit.length())));
        }

        return randomSb.toString();
    }

    public static void main(String[] args) throws Exception {
        String private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDO8bh72nmp5lvvW7gevF8UZM4g2qZKZCf4FF1077uWjIM7n76ARnwfnVlO2Ka4mJYrlnF1FZL78EQ18GGc2M5ESbt9hbuwdxmAbuniPngbTP0zsE6o7qXuXQlqanMxqu669qEGuSY/YrRUSD4bUP5fIKjCpXpVY5gA3ywCglSZymvao3U89dLVllhmyvlR86ZGQdHP7jQTxOyEjsuE4za3YmQjAlAje1GzGaszM1oVhnMvpxjmMEpnXpQEATiwNVC0kz8drPdCo37wozngpJTedQKI/f0wcLTQuQAz45G7hisdNP4qM8zVtwq5EiahZ4ge6cLHaDa1r4AW+m+zBZmVAgMBAAECggEAa+NbUb7CBRCix3Qi1mtQinpPeJNldI0oxU4gtxEw1DknRdNvZsLYfUb0Q4XxzXVHfSvutKLtwUku1owe572kM7fY9oTdKA+/jNTGPHxi86C8tNDrAWmBunZorUEYE0HROknQA4hNZ2hXKUWMk4MzSnblZK0OzSuiGHpBIIHAB3ivOJc94CTDIJkoaQD3IYB0iQ+vceVXRDHnAn5+2LXFzEWqYqhU6Y2Of3gkQvwnXi5OP7mSMKLIAT+yxyahVruCBO8DVzONjfxVDkrlzj69HZAlnGIt+wBJd8Z7aEtTKLW+bIgxZW6oL2k2bfzCRhYCU0pqnWqUFmCvYuvkxLlhIQKBgQD0hzJ4o2uuc1XB56dC9CSGLDxN5YZRJ7xCD5SLxBeuNsF5ePDyjpE2TZtCsWFYLE9ylgUfemumxJlcsrZbnDZHBRjf6aDk92Kwbfxv/fvt8jY+8XPkT8xvMHhyZlvqtuGuz/IFCPBft1Zv8gzFxRfG1lVwQ4MbZSN5UyQMwa6gqQKBgQDYpyNI7JFkl79VMoRrITCBrniRabjKV5yJ2JxIGxdidGPBzgEnnuvMypJgyAIMJLVoLYAu6qnF/T0F0KQqEceSaEScQGF7RsA9nRQLjK88P4+feyi0nCwWRbB6JsLqYkkv5ci3f1rtChtb7YvtvsvI2zYxgQgEu3QHhGclYuWJDQKBgEb+FW4m6/Vq8MPqEIqEvTHjF+L5t0RKiAKZ6WOmm8VtikBNF48kSw57+46iVwO1YZ9/5J7d+PEi9O+PxxfTw4Jc/XrMpnSzSgi09exBzemoP42Ipj/r1wuVA/MauqbrT5xkLPy/OqJfZQ39NS4Z6axFv+pm/2Jtu53WwImflaZ5AoGALWpL4TuF9tpAh3GBhJt+eZDDQWgyDmhzLgo0KIFVU5QmXTf70fxkXZeRO6xxkW4YgPAY0LzsPc6p/hAQeakkdrJYv5BXXuoj3kaRLyKckTqZtsqwa7vZGgodxl1duphD00CN5LlTrCDUP176WCoIueAJ6jKsJaHN3s2IebPTffkCgYBKI7OgI/nRFuap4jIpcKh4UUxrsI+CU1Zgp7B1wc5qdk7+t6zkpwIPD+Ef3pA7A0BzOCU+losd3JhoW3C9cCSkB4lXazNaO/BLtRORf6tGl32fvILiEr02J1E7GudplnbPiT6c1Obty7zpWq+9u7qHRa+Xd1T7P3vPN0G4O4CRpw==";
        String public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzvG4e9p5qeZb71u4HrxfFGTOINqmSmQn+BRddO+7loyDO5++gEZ8H51ZTtimuJiWK5ZxdRWS+/BENfBhnNjOREm7fYW7sHcZgG7p4j54G0z9M7BOqO6l7l0JampzMaruuvahBrkmP2K0VEg+G1D+XyCowqV6VWOYAN8sAoJUmcpr2qN1PPXS1ZZYZsr5UfOmRkHRz+40E8TshI7LhOM2t2JkIwJQI3tRsxmrMzNaFYZzL6cY5jBKZ16UBAE4sDVQtJM/Haz3QqN+8KM54KSU3nUCiP39MHC00LkAM+ORu4YrHTT+KjPM1bcKuRImoWeIHunCx2g2ta+AFvpvswWZlQIDAQAB";
        String encryptStr = encrypt("aa", public_key);
        System.out.println(encryptStr);
        boolean isvalidate = isvalidate(encryptStr, private_key);
        System.out.println(isvalidate);
        String decryptStr = decrypt(encryptStr, private_key);
        System.out.println(decryptStr);
    }
}
