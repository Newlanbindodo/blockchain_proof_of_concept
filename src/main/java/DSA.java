/**
 * @Auther:刘兰斌
 * @Date: 2021/06/07/17:43
 * @Explain:
 */


import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

class DSA {

    private static String src = "imooc security dsa";

    public static void main(String[] args) {
        String data = "zhuangzhang12yuan";
        dsatest(data);
    }

    public static String dsatest(String data) {
        String res = "";
        try {
            //1.初始化密钥
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(512);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            DSAPublicKey dsaPublicKey = (DSAPublicKey) keyPair.getPublic();
            DSAPrivateKey dsaPrivateKey = (DSAPrivateKey) keyPair.getPrivate();
            //2.执行签名
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(dsaPrivateKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1withDSA");
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(data.getBytes(StandardCharsets.UTF_8));
            signature.initSign(privateKey);
            signature.update(digest);
            byte[] result = signature.sign();
            res = Hex.encodeHexString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //3.验证签名
    //          isVerfy(data,result,dsaPublicKey);  String data,
    public static boolean isVerfy(String hash ,byte[] result, DSAPublicKey dsaPublicKey) throws NoSuchAlgorithmException {
        boolean bool = true;
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(dsaPublicKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1withDSA");
            signature.initVerify(dsaPublicKey);
        //    byte[] digest2 = MessageDigest.getInstance("SHA-256").digest(data.getBytes(StandardCharsets.UTF_8));
            signature.update(hash.getBytes(StandardCharsets.UTF_8));
            bool = signature.verify(result);//result是签名
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return bool;
    }
}
