package io.bluetape4k.javers.repository.cache2k

import io.bluetape4k.cache.cache2k.cache2k
import io.bluetape4k.javers.codecs.GsonCodec
import io.bluetape4k.javers.codecs.GsonCodecs
import io.bluetape4k.javers.repository.AbstractCdoSnapshotRepository
import org.cache2k.Cache
import org.javers.core.commit.CommitId
import org.javers.core.metamodel.`object`.CdoSnapshot

/**
 * [CdoSnapshot] 저장소로 [io.bluetape4k.infra.cache.cache2k.cache2k] 를 사용하는 Repository 입니다.
 *
 * @param codec [CdoSnapshot] 변환을 위한 [GsonCodec] 인스턴스
 */
class Cache2KCdoSnapshotRepository(
    codec: GsonCodec<String> = GsonCodecs.LZ4String,
): AbstractCdoSnapshotRepository<String>(codec) {

    private val snapshotCache: Cache<String, MutableList<String>> by lazy {
        cache2k<String, MutableList<String>> {
            this.entryCapacity(100_000)
            this.storeByReference(true)
            this.eternal(true)
        }.build()
    }

    private val commitSeqCache: Cache<CommitId, Long> by lazy {
        cache2k<CommitId, Long> {
            this.entryCapacity(100_000)
            this.eternal(true)
        }.build()
    }

    override fun getKeys(): List<String> {
        return snapshotCache.keys().toList()
    }

    override fun contains(globalIdValue: String): Boolean {
        return snapshotCache.containsKey(globalIdValue)
    }

    override fun getSeq(commitId: CommitId): Long = commitSeqCache[commitId] ?: 0L

    override fun updateCommitId(commitId: CommitId, sequence: Long) {
        commitSeqCache.put(commitId, sequence)
    }

    override fun getSnapshotSize(globalIdValue: String): Int {
        return snapshotCache[globalIdValue]?.size ?: 0
    }

    override fun saveSnapshot(snapshot: CdoSnapshot) {
        synchronized(this) {
            val globalIdValue = snapshot.globalId.value()
            val snapshots = snapshotCache.computeIfAbsent(globalIdValue) { mutableListOf() }
            val encoded = encode(snapshot)
            snapshots.add(0, encoded)
        }
    }

    override fun loadSnapshots(globalIdValue: String): List<CdoSnapshot> {
        return snapshotCache[globalIdValue]?.mapNotNull { decode(it) } ?: emptyList()
    }

}
