package io.bluetape4k.data.javers.repository.cache2k

import com.google.gson.JsonObject
import io.bluetape4k.data.javers.codecs.CdoSnapshotCodec
import io.bluetape4k.data.javers.codecs.JacksonCdoShapshotCodec
import io.bluetape4k.data.javers.repository.AbstractCdoRepository
import io.bluetape4k.infra.cache.cache2k.cache2k
import org.cache2k.Cache
import org.javers.core.commit.CommitId
import org.javers.core.metamodel.`object`.CdoSnapshot

class Cache2kCdoRepository(
    codec: CdoSnapshotCodec<String> = JacksonCdoShapshotCodec(),
): AbstractCdoRepository<String>(codec) {

    private val snapshotCache: Cache<String, MutableList<String>> =
        cache2k<String, MutableList<String>> {
            this.entryCapacity(100_000)
            this.storeByReference(true)
            this.eternal(true)
        }.build()

    private val commitSeqCache: Cache<CommitId, Long> =
        cache2k<CommitId, Long> {
            this.entryCapacity(100_000)
            this.eternal(true)
        }.build()

    override fun getKeys(): List<String> {
        return snapshotCache.keys().toList()
    }

    override fun contains(globalIdValue: String): Boolean {
        return snapshotCache.containsKey(globalIdValue)
    }

    override fun getSeq(commitId: CommitId): Long = commitSeqCache[commitId]!!

    override fun updateCommitId(commitId: CommitId, sequence: Long) {
        commitSeqCache.put(commitId, sequence)
    }

    override fun getSnapshotSize(globalIdValue: String): Int {
        return snapshotCache[globalIdValue]?.size ?: 0
    }

    override fun saveSnapshot(snapshot: CdoSnapshot) {
        val globalIdValue = snapshot.globalId.value()
        val snapshots = snapshotCache.computeIfAbsent(globalIdValue) { mutableListOf() }
        val encoded = encode(snapshot)
        snapshots.add(encoded)
    }

    override fun loadSnapshots(globalIdValue: String): List<CdoSnapshot> {
        return snapshotCache[globalIdValue]?.mapNotNull { decode(it) } ?: emptyList()
    }

    override fun doEncode(jsonObject: JsonObject): String {
        return codec.encode(jsonObject)
    }

    override fun doDecode(data: String): JsonObject? {
        return codec.decode(data)
    }

}
