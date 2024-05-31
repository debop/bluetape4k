@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")
@file:Suppress("UNCHECKED_CAST")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.utils.NULL_VALUE
import io.bluetape4k.support.requireNotEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Inspirited by [NgRx memoized selector](https://ngrx.io/guide/store/selectors).
 *
 * Selector는 상태 흐름의 조각을 얻는 데 사용되는 순수 함수입니다.
 * `FlowExt` provides a few helper functions for optimizing this selection.
 *
 * - 셀렉터는 파생된 데이터를 계산하여 가능한 최소한의 상태를 저장할 수 있습니다.
 * - 선택자는 효율적입니다. 인자 중 하나가 변경되지 않는 한 선택자는 다시 계산되지 않습니다.
 * - [select] 함수를 사용할 때 선택기 함수가 호출된 최신 인수를 추적합니다.
 *   선택자는 순수 함수이기 때문에 인수가 일치하면 선택자 함수를 다시 호출하지 않고도 마지막 결과를 반환할 수 있습니다.
 *   이는 특히 고비용 계산을 수행하는 셀렉터에서 성능 이점을 제공할 수 있습니다.
 *   이러한 기법을 `Momization` 이라고 합니다. (Memorization 이라고 Cache에서 만든 것처럼 ㅎㅎ)
 */
typealias Selector<State, SubState> = suspend (State) -> SubState

/**
 * [State]에서 하위 상태를 선택하고 이전 상태와 다른 경우 방출합니다.
 *
 * @param selector [State]를 받아 하위 상태(`State`) 를 반환하는 함수
 */
fun <State, Result> Flow<State>.select(selector: Selector<State, Result>): Flow<Result> =
    map(selector).distinctUntilChanged()

/**
 * 소스 [Flow]에서 두 개의 하위 상태를 선택하고 이를 [Result]로 결합합니다.
 *
 * [projector] 하위 상태 중 하나가 변경될 때만 호출됩니다.
 * 반환되는 flow 는 [projector]의 결과가 방출됩니다.
 * 그리고 모든 후속 결과가 동일한 값인 경우 필터링됩니다. ([distinctUntilChanged]와 같은 기능입니다.
 *
 * @param selector1 첫 번째 하위 상태를 선택하는 데 사용되는 첫 번째 선택기입니다.
 * @param selector2 두 번째 하위 상태를 선택하는 데 사용되는 두 번째 선택기입니다.
 * @param projector 하위 상태를 결과로 결합하는 데 사용할 프로젝터입니다.
 */
fun <State, SubState1, SubState2, Result> Flow<State>.select(
    selector1: Selector<State, SubState1>,
    selector2: Selector<State, SubState2>,
    projector: suspend (SubState1, SubState2) -> Result,
): Flow<Result> = selectInternal(
    selectors = arrayOf(selector1, selector2),
    projector = { projector(it[0] as SubState1, it[1] as SubState2) }
)

/**
 * 소스 [Flow]에서 두 개의 하위 상태를 선택하고 이를 [Result]로 결합합니다.
 *
 * [projector] 하위 상태 중 하나가 변경될 때만 호출됩니다.
 * 반환되는 flow 는 [projector]의 결과가 방출됩니다.
 * 그리고 모든 후속 결과가 동일한 값인 경우 필터링됩니다. ([distinctUntilChanged]와 같은 기능입니다.
 *
 * @param selector1 첫 번째 하위 상태를 선택하는 데 사용되는 첫 번째 선택기입니다.
 * @param selector2 두 번째 하위 상태를 선택하는 데 사용되는 두 번째 선택기입니다.
 * @param selector3 세 번째 하위 상태를 선택하는 데 사용되는 세 번째 선택기입니다.
 * @param projector 하위 상태를 결과로 결합하는 데 사용할 프로젝터입니다.
 */
fun <State, SubState1, SubState2, SubState3, Result> Flow<State>.select(
    selector1: Selector<State, SubState1>,
    selector2: Selector<State, SubState2>,
    selector3: Selector<State, SubState3>,
    projector: suspend (SubState1, SubState2, SubState3) -> Result,
): Flow<Result> = selectInternal(
    selectors = arrayOf(selector1, selector2, selector3),
    projector = {
        projector(
            it[0] as SubState1,
            it[1] as SubState2,
            it[2] as SubState3
        )
    }
)

/**
 * 소스 [Flow]에서 두 개의 하위 상태를 선택하고 이를 [Result]로 결합합니다.
 *
 * [projector] 하위 상태 중 하나가 변경될 때만 호출됩니다.
 * 반환되는 flow 는 [projector]의 결과가 방출됩니다.
 * 그리고 모든 후속 결과가 동일한 값인 경우 필터링됩니다. ([distinctUntilChanged]와 같은 기능입니다.
 *
 * @param selector1 첫 번째 하위 상태를 선택하는 데 사용되는 첫 번째 선택기입니다.
 * @param selector2 두 번째 하위 상태를 선택하는 데 사용되는 두 번째 선택기입니다.
 * @param selector3 세 번째 하위 상태를 선택하는 데 사용되는 세 번째 선택기입니다.
 * @param selector3 네 번째 하위 상태를 선택하는 데 사용되는 네 번째 선택기입니다.
 * @param projector 하위 상태를 결과로 결합하는 데 사용할 프로젝터입니다.
 */
fun <State, SubState1, SubState2, SubState3, SubState4, Result> Flow<State>.select(
    selector1: Selector<State, SubState1>,
    selector2: Selector<State, SubState2>,
    selector3: Selector<State, SubState3>,
    selector4: Selector<State, SubState4>,
    projector: suspend (SubState1, SubState2, SubState3, SubState4) -> Result,
): Flow<Result> = selectInternal(
    selectors = arrayOf(selector1, selector2, selector3, selector4),
    projector = {
        projector(
            it[0] as SubState1,
            it[1] as SubState2,
            it[2] as SubState3,
            it[3] as SubState4,
        )
    }
)

/**
 * 소스 [Flow]에서 두 개의 하위 상태를 선택하고 이를 [Result]로 결합합니다.
 *
 * [projector] 하위 상태 중 하나가 변경될 때만 호출됩니다.
 * 반환되는 flow 는 [projector]의 결과가 방출됩니다.
 * 그리고 모든 후속 결과가 동일한 값인 경우 필터링됩니다. ([distinctUntilChanged]와 같은 기능입니다.
 *
 * @param selector1 첫 번째 하위 상태를 선택하는 데 사용되는 첫 번째 선택기입니다.
 * @param selector2 두 번째 하위 상태를 선택하는 데 사용되는 두 번째 선택기입니다.
 * @param selector3 세 번째 하위 상태를 선택하는 데 사용되는 세 번째 선택기입니다.
 * @param selector3 네 번째 하위 상태를 선택하는 데 사용되는 네 번째 선택기입니다.
 * @param projector 하위 상태를 결과로 결합하는 데 사용할 프로젝터입니다.
 */
fun <State, SubState1, SubState2, SubState3, SubState4, SubState5, Result> Flow<State>.select(
    selector1: Selector<State, SubState1>,
    selector2: Selector<State, SubState2>,
    selector3: Selector<State, SubState3>,
    selector4: Selector<State, SubState4>,
    selector5: Selector<State, SubState5>,
    projector: suspend (SubState1, SubState2, SubState3, SubState4, SubState5) -> Result,
): Flow<Result> = selectInternal(
    selectors = arrayOf(selector1, selector2, selector3, selector4, selector5),
    projector = {
        projector(
            it[0] as SubState1,
            it[1] as SubState2,
            it[2] as SubState3,
            it[3] as SubState4,
            it[4] as SubState5,
        )
    }
)

private typealias SubStateT = Any?

private fun <State, Result> Flow<State>.selectInternal(
    selectors: Array<Selector<State, SubStateT>>,
    projector: suspend (Array<SubStateT>) -> Result,
): Flow<Result> {
    selectors.requireNotEmpty("selectors")

    return flow {
        var latestSubStates: Array<SubStateT>? = null
        var latestState: Any? = NULL_VALUE
        var reusableSubStates: Array<SubStateT>? = null

        collect { state ->
            val currentSubStates =
                reusableSubStates ?: arrayOfNulls<SubStateT>(selectors.size).also { reusableSubStates = it }

            selectors.indices.forEach {
                currentSubStates[it] = selectors[it](state)
            }

            if (latestSubStates === null || !currentSubStates.contentEquals(latestSubStates)) {
                val currentState = projector(
                    currentSubStates.copyOf().also { latestSubStates = it }
                )
                if (latestState === NULL_VALUE || (latestState as Result) != currentState) {
                    latestState = currentState
                    emit(currentState)
                }
            }
        }
    }
}
