import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;


/**
 * @Auther:刘兰斌
 * @Date: 2021/07/05/20:33
 * @Explain:
 */
public class Block {
    private ArrayList<Transaction> transactions;//data
    private String prehash;
    private String curhash;
    private Integer nonce;
    private Long timestamp;
    public Block() {
    }
    public Block(ArrayList<Transaction> transactions, String prehash) {
        this.transactions = transactions;
        this.prehash = prehash;
        this.curhash = computeHash();
        this.timestamp = System.currentTimeMillis();
        nonce = 1;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getPrehash() {
        return prehash;
    }

    public void setPrehash(String prehash) {
        this.prehash = prehash;
    }

    public String getCurhash() {
        return curhash;
    }

    public void setCurhash(String curhash) {
        this.curhash = curhash;
    }
    //得到消息的hash值
    public static String getSHA256(String string) {
        String res="";
        try {
            byte[] digestmessage = MessageDigest.getInstance("SHA-256").digest(string.getBytes(StandardCharsets.UTF_8));
            res = Hex.encodeHexString(digestmessage);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return res;
    }
    //封装一个计算块hash值的方法
    public String computeHash(){
        //将对象集合转化为字符串JSON.toJSONString
        return Block.getSHA256(JSON.toJSONString(this.transactions) +this.getPrehash()+this.nonce+this.timestamp);
    }
    //获取难度值，这里难度值用比特币系统中计算前几位0为例
    public String getDifficultyValue(int nando) {
        String res = "";
        for (int i = 0; i < nando; i++) {
            res += "0";
        }
        return res;
    }
    //挖矿去找符合难度的hash值
    public String mine(int nando, DSAPublicKey dsaPublicKey, byte[] sign) throws Exception {
        System.out.println("交易打包入块时一切正常！");
        while (true) {
            this.curhash = this.computeHash();
            if (!this.curhash.substring(0, nando).equals(this.getDifficultyValue(nando))) {
                this.nonce++;
                this.curhash = this.computeHash();
            } else {
                break;
            }
        }
        return this.curhash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "transactions=" + transactions +
                ", prehash='" + prehash + '\'' +
                ", curhash='" + curhash + '\'' +
                ", nonce=" + nonce +
                '}';
    }
    //    @Test
//    public void test(){
//        Block block = new Block("转账十元","123");
//        System.out.println(block);
//    }
}
