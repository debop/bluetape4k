package io.bluetape4k.data.javers.repository.caffeine

import com.github.benmanes.caffeine.cache.Cache
import com.google.gson.JsonObject
import io.bluetape4k.data.javers.codecs.CdoSnapshotCodec
import io.bluetape4k.data.javers.repository.AbstractCdoRepository
import io.bluetape4k.infra.cache.caffeine.caffeine
import org.javers.core.commit.CommitId
import org.javers.core.metamodel.`object`.CdoSnapshot

class CaffeineCdoRepository(
    codec: CdoSnapshotCodec<ByteArray> = DEFAULT_CODEC,
): AbstractCdoRepository<ByteArray>(codec) {

    /**
     * [CdoSnapshot] 컬렉션을 저장하는 Cache (key=globalId, value=collection of encoded snapshot)
     */
    private val snapshotCache: Cache<String, MutableList<ByteArray>> by lazy {
        caffeine {
            initialCapacity(1_000)
        }.build()
    }

    /**
     * [CommitId] - Sequence Number를 캐시합니다.
     */
    private val commitSeqCache: Cache<CommitId, Long> by lazy {
        caffeine {
            initialCapacity(1_000)
        }.build()
    }

    override fun getKeys(): List<String> {
        return snapshotCache.asMap().map { it.key }
    }

    override fun contains(globalIdValue: String): Boolean {
        return snapshotCache.getIfPresent(globalIdValue) != null
    }

    override fun getSeq(commitId: CommitId): Long {
        return commitSeqCache.getIfPresent(commitId)!!
    }

    override fun updateCommitId(commitId: CommitId, sequence: Long) {
        commitSeqCache.put(commitId, sequence)
    }

    override fun getSnapshotSize(globalIdValue: String): Int {
        return snapshotCache.getIfPresent(globalIdValue)?.size ?: 0
    }

    override fun saveSnapshot(snapshot: CdoSnapshot) {
        val globalIdValue = snapshot.globalId.value()
        val snapshots = snapshotCache.get(globalIdValue) { key -> mutableListOf() }
        val encoded = encode(snapshot)
        snapshots.add(encoded)
    }

    override fun loadSnapshots(globalIdValue: String): List<CdoSnapshot> {
        return snapshotCache.getIfPresent(globalIdValue)?.mapNotNull { decode(it) } ?: emptyList()
    }

    override fun doEncode(jsonObject: JsonObject): ByteArray {
        return codec.encode(jsonObject)
    }

    override fun doDecode(data: ByteArray): JsonObject? {
        return codec.decode(data)
    }
}
