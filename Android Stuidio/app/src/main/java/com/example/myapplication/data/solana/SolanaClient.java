package com.example.myapplication.data.solana;

import org.p2p.solanaj.rpc.RpcClient;

public class SolanaClient {

    private static final String RPC_ENDPOINT = "https://api.mainnet-beta.solana.com";
    private static RpcClient rpcClient;

    public static RpcClient getInstance() {
        if (rpcClient == null) {
            rpcClient = new RpcClient(RPC_ENDPOINT);
        }
        return rpcClient;
    }
}
