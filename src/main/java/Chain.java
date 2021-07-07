import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther:刘兰斌
 * @Date: 2021/07/05/20:34
 * @Explain:
 */
public class Chain {
    private ArrayList<Block> chain;
    //交易池
    private ArrayList<Transaction> transactionpool = new ArrayList<>();
    private Integer reward;
    private Integer nando;
    public Chain() {
        this.chain = new ArrayList<>();
        this.chain.add(new Block(firstTransaction(), ""));
        reward = 50;
        nando = 4;
    }

    @Override
    public String toString() {
        return "Chain{" +
                "chain=" + chain +
                ", transactionpool=" + transactionpool +
                ", reward=" + reward +
                ", nando=" + nando +
                '}';
    }

    //创建第一笔创世交易,初始化链时加入
    public ArrayList<Transaction> firstTransaction() {
        ArrayList<Transaction> firsttransaction = new ArrayList<>();
        firsttransaction.add(new Transaction(null, "祖先", 50));
        return firsttransaction;
    }
    //挖出交易打包成块
//    public void packingBlock(String miner) throws Exception {
//        //区块链发放一笔奖励交易
//        Transaction transaction = new Transaction("", miner, 50);
//        //交易入库
//        this.transactionpool.add(transaction);
//        //简化流程，将所有transaction都从交易池中挖出来
//        Block block = new Block(this.transactionpool,this.chain.get(this.chain.size()-1).getCurhash());
//        block.mine(4);
//        System.out.println("挖矿结束！挖到了"+block.mine(4));
//        this.chain.add(block);
//        this.transactionpool = null;
//    }
    public void minetransaction(String mineadress,DSAPublicKey dsaPublicKey,byte[] sign) throws Exception {
        //发放矿工奖励
        Transaction rewardtransaction = new Transaction("", mineadress, this.reward);
        this.transactionpool.add(rewardtransaction);
        //注意当调用 this.transactionpool.clear();方法时，下面所有的transaction均为0，问题的关键是怎么解决这个引用地址问题
        ArrayList<Transaction> copypooladress = new ArrayList<>();
        for (int i = 0; i <= transactionpool.size() - 1; i++) {
            copypooladress.add(transactionpool.get(i));
        }
        Block blocks = new Block(copypooladress,this.chain.get(this.chain.size()-1).getCurhash());//
        //blocks.mine(this.dificultnum);
        String curhash = blocks.mine(this.nando,dsaPublicKey,sign);
        if (curhash != null) {
            System.out.println("挖矿结束！挖到了"+ curhash);
        //    addNode(blocks, copypooladress, curhash);
            this.chain.add(blocks);
            //添加区块到链
            //更改地址引用。为了删除交易池中数据。
            //清空交易池
            this.transactionpool.clear();
        }
    }
    //查看链上的某笔交易
    public void showTransaction(int blockindex){
        System.out.println(this.chain.get(blockindex).getTransactions());
    }
    //添加交易到交易池中
//    public void addTransactionpool(Transaction transaction){
//        if (transaction == null){
//            System.out.println("交易为空");
//        }else{
//            this.transactionpool.add(transaction);
//        }
//
//    }
    public void addTransactionpool(Transaction transaction, DSAPublicKey dsaPublicKey, byte[] sign) throws Exception {
        try {
            if (!transaction.isVerified(dsaPublicKey,sign)){
                throw new Exception("不合法的交易,公钥错误或者数据被篡改,交易不能添加到交易池中！");
            }
                System.out.println("合法的交易！");
            if (transaction == null) {
                System.out.println("传入的数据为空");
            } else {
                this.transactionpool.add(transaction);
            }
        } catch (Exception n) {
            n.printStackTrace();
        }

    }
    //添加块到原有链上,手动自行打包成块
//    public ArrayList<Block> addBlockToChain(Block newblock) {
//        String lasthash = chain.get(chain.size() - 1).getCurhash();
//        newblock.setPrehash(lasthash);
//        //由挖矿得到一个符合条件的hash值
//        newblock.setCurhash(newblock.mine(nando));
//        System.out.println("挖矿结束！"+newblock.mine(nando));
//        chain.add(newblock);
//        return chain;
//    }
    /*
        验证区块数据合法性，前一个区块的hash值是否等于当前区块的prehash值
            1）如果这里仅仅是在篡改数据，那么原先的curhash是不会变化的，因此只要比较新计算的curhash与原hash不同即可
            2）还有一种除了篡改完数据，还想进一步篡改当前hash值，这种属于顾头不顾腚。只需要将当前你伪造的hash值与原先的hash值进行比较即可
        上述2)中不一致是问题所在，因此还有一种疯狂做法，一直往前改hash,就很头铁。
     */
    //解决问题1）
    public boolean checkData() {
        //将取得的区块的hash值与计算当前区块hash值比较，如果不相等则代表出错。
        List<Boolean> collect = chain.stream()
                .map(s -> {
                    if (!s.getCurhash().equals(s.computeHash())) {//Block.getSHA256(s.getData() + s.getPrehash()
                        System.out.println("数据发生篡改！");
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        return collect.contains(false) ? false:true;
    }
    //解决问题2）
    public boolean checkConn(){
        for (int i = 1;i<chain.size();i++){
            Block block = chain.get(i);
            Block preblock = chain.get(i-1);
            //如果当前区块的prehash值等于前一个区块的hash值，则说明暂时没有篡改hash值
            if (!block.getPrehash().equals(preblock.getCurhash())) {
                System.out.println("存在篡改hash值的问题！");
                return false;
            }
        }
        return true;
    }
    //汇总封装解决方案
    public boolean checkAll(){
        return this.checkConn() && this.checkData()  ? true:false;
    }

 //   @Test
//    public void test(){
//        Chain chain = new Chain();
//        Transaction transaction1= new Transaction("addr1", "addr2", 15);
//        Transaction transaction2= new Transaction("addr2", "addr1", 8);
//        chain.addTransactionpool(transaction1);
//        chain.addTransactionpool(transaction2);
//        System.out.println(chain);
//        chain.packingBlock("addr3");
//        System.out.println(chain);
//    }
//    public void add(){
//        Chain chain = new Chain();
//        Block newblock = new Block("转账十元","");
//        Block newblock2 = new Block("转账十个十元","");
//        chain.addBlockToChain(newblock);
//        chain.addBlockToChain(newblock2);
//
//        //尝试篡改数据，并修改hash值
//        chain.chain.get(1).setData("转账1000");
//        chain.chain.get(1).setCurhash(chain.chain.get(1).mine(2));
//        System.out.println(chain.checkAll());
//
//        System.out.println(chain);
//    }
}
