package io.bluetape4k.utils.math.equation

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver

abstract class AbstractEquator: Equator {

    protected abstract val solver: BaseUnivariateSolver<UnivariateFunction>

    override val absoluteAccuracy: Double
        get() = solver.absoluteAccuracy

    override fun solve(maxEval: Int, min: Double, max: Double, evaluator: (Double) -> Double): Double {
        return solver.solve(maxEval, UnivariateFunction { x: Double -> evaluator(x) }, min, max)
    }
}
