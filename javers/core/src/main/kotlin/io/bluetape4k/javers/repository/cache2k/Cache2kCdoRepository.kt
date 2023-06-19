package io.bluetape4k.javers.repository.cache2k

import io.bluetape4k.infra.cache.cache2k.cache2k
import io.bluetape4k.javers.codecs.CdoSnapshotCodec
import io.bluetape4k.javers.codecs.CdoSnapshotCodecs
import io.bluetape4k.javers.repository.AbstractCdoRepository
import org.cache2k.Cache
import org.javers.core.commit.CommitId
import org.javers.core.metamodel.`object`.CdoSnapshot

class Cache2kCdoRepository(
    codec: CdoSnapshotCodec<ByteArray> = CdoSnapshotCodecs.Default,
): AbstractCdoRepository<ByteArray>(codec) {

    private val snapshotCache: Cache<String, MutableList<ByteArray>> =
        cache2k<String, MutableList<ByteArray>> {
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
        snapshots.add(0, encoded)
    }

    override fun loadSnapshots(globalIdValue: String): List<CdoSnapshot> {
        return snapshotCache[globalIdValue]?.mapNotNull { decode(it) } ?: emptyList()
    }

}
