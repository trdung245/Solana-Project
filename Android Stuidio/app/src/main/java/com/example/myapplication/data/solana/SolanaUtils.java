package com.example.myapplication.data.solana;

import android.util.Log;
import org.bitcoinj.core.Base58;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;


public class SolanaUtils {
    private static final String TAG = "SolanaUtils";

    public static String createNewWallet() {
        Account account = new Account();
        PublicKey publicKey = account.getPublicKey();
        return Base58.encode(publicKey.toByteArray());
    }

    public static void fetchWalletBalance(String walletAddress) {
        RpcClient client = SolanaClient.getInstance();

        new Thread(() -> {
            try {
                PublicKey publicKey = new PublicKey(Base58.decode(walletAddress));
                long balance = client.getApi().getBalance(publicKey);
                Log.d(TAG, "Wallet Balance: " + balance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}

