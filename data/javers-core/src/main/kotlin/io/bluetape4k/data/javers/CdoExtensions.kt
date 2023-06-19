package io.bluetape4k.data.javers

import org.javers.core.commit.CommitId
import org.javers.core.graph.Cdo
import org.javers.core.metamodel.`object`.CdoSnapshot
import org.javers.core.metamodel.`object`.SnapshotType
import org.javers.repository.api.QueryParams
import kotlin.jvm.optionals.getOrNull

fun Cdo.getWrappedOrNull(): Any? = this.wrappedCdo.getOrNull()

fun <R> CdoSnapshot.mapProperties(mapper: (key: String, value: Any?) -> R): List<R> =
    this.state.mapProperties(mapper)

fun <R> CdoSnapshot.forEachProperties(consumer: (key: String, value: Any?) -> Unit): Unit =
    this.state.forEachProperty(consumer)

fun Sequence<CdoSnapshot>.filterByToCommitId(commitId: CommitId): Sequence<CdoSnapshot> =
    filter { it.commitId.isBeforeOrEqual(commitId) }

fun Sequence<CdoSnapshot>.filterByCommitIds(commitIds: Collection<CommitId>): Sequence<CdoSnapshot> =
    filter { commitIds.contains(it.commitId) }

fun Sequence<CdoSnapshot>.filterByVersion(version: Long): Sequence<CdoSnapshot> =
    filter { it.version == version }

fun Sequence<CdoSnapshot>.filterByAuthor(author: String): Sequence<CdoSnapshot> =
    filter { it.commitMetadata.author == author }

fun Sequence<CdoSnapshot>.filterByCommitDate(queryParams: QueryParams): Sequence<CdoSnapshot> =
    filter { queryParams.isDateInRange(it.commitMetadata.commitDate) }

fun Sequence<CdoSnapshot>.filterByChangedPropertyName(propertyName: String): Sequence<CdoSnapshot> =
    filter { it.hasChangeAt(propertyName) }

fun Sequence<CdoSnapshot>.filterByChangedPropertyNames(propertyNames: Set<String>): Sequence<CdoSnapshot> =
    filter { snapshot ->
        propertyNames.any { propertyName ->
            snapshot.hasChangeAt(propertyName)
        }
    }

fun Sequence<CdoSnapshot>.filterByType(snapshotType: SnapshotType): Sequence<CdoSnapshot> =
    filter { it.type == snapshotType }

fun Sequence<CdoSnapshot>.filterByCommitProperties(
    commitProperties: Map<String, Collection<String>>,
): Sequence<CdoSnapshot> =
    filter {
        val props = it.commitMetadata.properties
        commitProperties.all { (key, values) ->
            props.containsKey(key) && values.contains(props[key])
        }
    }

fun Sequence<CdoSnapshot>.trimToRequestedSlice(skip: Int, limit: Int): List<CdoSnapshot> =
    drop(skip).take(limit).toList()
