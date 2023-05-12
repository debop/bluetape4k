package io.bluetape4k.utils.naivebayes

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.toUnifiedMap
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.collections.eclipse.unifiedMapOf
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import org.eclipse.collections.api.list.ImmutableList
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.map.mutable.UnifiedMap
import kotlin.math.exp
import kotlin.math.ln


/**
 * A `NaiveBayesClassifier` that associates each set of `F` features from an item `T` with a category `C`.
 * New sets of features `F` can then be used to predict a category `C`.
 *
 * @param F
 * @param C
 * @property observationLimit
 * @property k1
 * @property k2
 */
class NaiveBayesClassifier<F: Any, C: Any>(
    private val observationLimit: Int = Int.MAX_VALUE,
    val k1: Double = DEFAULT_K1,
    val k2: Double = DEFAULT_K2,
) {

    companion object: KLogging() {
        const val DEFAULT_K1: Double = 0.5
        const val DEFAULT_K2: Double = DEFAULT_K1 * 2.0
    }

    @Volatile
    private var probabilities: UnifiedMap<FeatureProbability.Key<F, C>, FeatureProbability<F, C>> =
        unifiedMapOf<FeatureProbability.Key<F, C>, FeatureProbability<F, C>>()

    private val _population: FastList<BayesInput<F, C>> = fastListOf<BayesInput<F, C>>()
    val population: ImmutableList<BayesInput<F, C>> get() = _population.toImmutable()

    private val modelStaler = atomic(false)
    private var modelStaled: Boolean by modelStaler

    /**
     * Adds an observation of features to a category
     */
    fun addObservation(category: C, features: Iterable<F>) {
        if (_population.size == observationLimit) {
            _population.removeAt(0)
        }
        _population += BayesInput(category, features.toUnifiedSet())
        modelStaler.value = true
    }

    /**
     * Adds an observation of features to a category
     */
    fun addObservation(category: C, vararg features: F) {
        addObservation(category, features.toSet())
    }

    private fun rebuildModel() {
        probabilities = _population
            .flatMap { it.features }
            .distinct()
            .flatMap { f ->
                _population
                    .map { it.category }
                    .distinct()
                    .map { c -> FeatureProbability.Key(f, c) }
            }
            .map { it to FeatureProbability(it.feature, it.category, this) }
            .toUnifiedMap()

        modelStaler.value = false
    }

    /**
     * Returns the categories that have been captured by the model so far.
     */
    val categories: Set<C>
        get() = probabilities.keys.map { it.category }.toUnifiedSet()

    /**
     *  Predicts a category `C` for a given set of `F` features
     */
    fun predict(vararg features: F): C? = predictWithProbability(features.toSet())?.category

    /**
     * Predicts a category `C` for a given set of `F` features
     */
    fun predict(features: Iterable<F>): C? = predictWithProbability(features)?.category


    /**
     *  Predicts a category `C` for a given set of `F` features,
     *  but also returns the probability of that category being correct.
     */
    fun predictWithProbability(features: Iterable<F>): CategoryProbability<C>? {
        if (modelStaled) {
            rebuildModel()
        }

        val f = features.toSet()

        return categories.asSequence()
            .filter { c ->
                population.any { it.category == c } && probabilities.values.any { it.feature in f }
            }
            .map { c ->
                val probIfCategory = probabilities.values
                    .filter { it.category == c }
                    .map {
                        if (it.feature in f) {
                            ln(it.probability)
                        } else {
                            ln(1.0 - it.probability)
                        }
                    }
                    .sum()
                    .run { exp(this) }

                val probIfNotCategory = probabilities.values
                    .filter { it.category == c }
                    .map {
                        if (it.feature in f) {
                            ln(it.notProbability)
                        } else {
                            ln(1.0 - it.notProbability)
                        }
                    }
                    .sum()
                    .run { exp(this) }

                CategoryProbability(
                    category = c,
                    probability = probIfCategory / (probIfCategory + probIfNotCategory)
                )
            }
            .filter { it.probability >= 0.1 }
            .sortedByDescending { it.probability }
            .firstOrNull()
    }

    class FeatureProbability<F: Any, C: Any>(val feature: F, val category: C, nbc: NaiveBayesClassifier<F, C>) {

        data class Key<F, C>(val feature: F, val category: C)

        val probability: Double =
            (nbc.k1 + nbc.population.count { it.category == category && feature in it.features }) /
                (nbc.k2 + nbc.population.count { it.category == category })

        val notProbability: Double =
            (nbc.k1 + nbc.population.count { it.category != category && feature in it.features }) /
                (nbc.k2 + nbc.population.count { it.category != category })

        val key: Key<F, C> get() = Key(feature, category)
    }
}
