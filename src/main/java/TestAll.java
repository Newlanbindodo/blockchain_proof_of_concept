import org.apache.commons.codec.binary.Hex;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;

/**
 * @Auther:刘兰斌
 * @Date: 2021/07/07/9:02
 * @Explain:
 */
public class TestAll {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        DSAPublicKey dsaPublicKey = (DSAPublicKey) keyPair.getPublic();
        DSAPrivateKey dsaPrivateKey = (DSAPrivateKey) keyPair.getPrivate();

        KeyPairGenerator keyPairGenerator2 = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator2.initialize(512);
        KeyPair keyPair2 = keyPairGenerator2.generateKeyPair();
        DSAPublicKey dsaPublicKey2 = (DSAPublicKey) keyPair2.getPublic();
        DSAPrivateKey dsaPrivateKey2 = (DSAPrivateKey) keyPair2.getPrivate();

        Transaction transaction1 = new Transaction(Hex.encodeHexString(dsaPublicKey.getEncoded()),Hex.encodeHexString(dsaPublicKey2.getEncoded()),10);
        System.out.println(transaction1);
        //开始签名
        byte[] sign = transaction1.sign(dsaPrivateKey);
        String signature = Hex.encodeHexString(sign);
        System.out.println("得到的签名是"+signature);
        //开始验证
        transaction1.isVerified(dsaPublicKey,sign);
        Chain chain = new Chain();
        chain.addTransactionpool(transaction1,dsaPublicKey,sign);
        chain.minetransaction("addr3",dsaPublicKey,sign);
        System.out.println(chain);
 //       chain.showTransaction(1);
    }
}
