import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @Auther:刘兰斌
 * @Date: 2021/07/05/20:34
 * @Explain:
 */
public class Transaction {
    private String transferor;
    private String payee;
    private Integer mount;

    public Transaction(String transferor, String payee, Integer mount) {
        transferor = transferor;
        this.payee = payee;
        this.mount = mount;
    }

    public String getTransferor() {
        return transferor;
    }

    public void setTransferor(String transferor) {
        this.transferor = transferor;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public Integer getMount() {
        return mount;
    }

    public void setMount(Integer mount) {
        this.mount = mount;
    }

    public String computeHash(){
        return Block.getSHA256(this.transferor+this.payee+this.mount);
    }
    //签名
    public byte[] sign(DSAPrivateKey privatekeyPair) {
        // String signature = DSA.dsatest(this.sender + this.receiver + this.amount);
        //  String res = "";
        byte[] result=null;
        try {

            //2.执行签名 dsaPrivateKey
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privatekeyPair.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1withDSA");
            //    byte[] digest = MessageDigest.getInstance("SHA-256").digest(data.getBytes(StandardCharsets.UTF_8));
            signature.initSign(privateKey);
            signature.update(computeHash().getBytes(StandardCharsets.UTF_8));
            result = signature.sign();
            // res = Hex.encodeHexString(result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

    //验证
    public boolean isVerified(DSAPublicKey dsapublickey, byte[] signature) throws NoSuchAlgorithmException {
        if(this.transferor==""){
            return true;
        }
       return DSA.isVerfy(computeHash(),signature,dsapublickey);
    }
    //获取上面验证的一个bool值、实际上是对验证方法的一种封装

    @Override
    public String toString() {
        return "Transaction{" +
                "Transferor='" + transferor + '\'' +
                ", payee='" + payee + '\'' +
                ", mount=" + mount +
                '}';
    }



}
