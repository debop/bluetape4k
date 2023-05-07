package io.bluetape4k.utils.math.equation

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
import org.apache.commons.math3.analysis.solvers.SecantSolver

class SecantEquator: AbstractEquator() {

    override val solver: BaseUnivariateSolver<UnivariateFunction> = SecantSolver()

}