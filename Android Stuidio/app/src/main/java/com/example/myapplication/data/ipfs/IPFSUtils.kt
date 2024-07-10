package com.example.myapplication.data.ipfs

import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multihash.Multihash
import java.io.IOException

object IPFSUtils {
    private val ipfs = IPFS("/ip4/127.0.0.1/tcp/5001")

    @Throws(IOException::class)
    fun uploadFile(fileContent: ByteArray?, fileName: String?): String {
        val file = NamedStreamable.ByteArrayWrapper(fileName, fileContent)
        val addResult = ipfs.add(file)[0]
        return addResult.hash.toBase58()
    }

    @Throws(IOException::class)
    fun downloadFile(fileHash: String?): ByteArray {
        return ipfs.cat(Multihash.fromHex(fileHash))
    }
}