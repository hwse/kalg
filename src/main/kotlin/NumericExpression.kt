import java.math.BigInteger
import kotlin.math.sqrt

interface Expression {
    fun represent(): String
}

interface NumericExpression: Expression {
    fun calculate(exact: Boolean = false): NumericExpression
}

interface BooleanExpression: Expression {
    fun calculate(): BooleanExpression
}

interface RealNumber: NumericExpression {
    val doubleValue: Double
}

interface NaturalNumber: RealNumber {
    val longValue: BigInteger
}

class BoolConstant(val value: Boolean): BooleanExpression {
    override fun calculate(): BooleanExpression {
        return this
    }

    override fun represent(): String {
        return value.toString()
    }

}

class NaturalNumberConstant(override val longValue: BigInteger): NaturalNumber {
    override val doubleValue: Double
        get() = longValue.toDouble()

    override fun calculate(exact: Boolean): NumericExpression {
        return this
    }

    override fun represent(): String {
        return longValue.toString()
    }

}

class RealNumberConstant(override val doubleValue: Double): RealNumber {

    override fun calculate(exact: Boolean): NumericExpression {
        return this
    }

    override fun represent(): String {
        return doubleValue.format(4)
    }
}

class Variable(private val name: String): NumericExpression {
    override fun calculate(exact: Boolean): NumericExpression {
        return this
    }

    override fun represent(): String {
        return name
    }
}

class SquareRoot(private val of: NumericExpression): NumericExpression {
    override fun calculate(exact: Boolean): NumericExpression {
        val of = of.calculate(exact)
        if (of is NaturalNumber) {
            if (exact) {
                return RealNumberConstant(sqrt(of.doubleValue))
            } else {
                val exactSqrt = sqrt(of.doubleValue)
                if ((exactSqrt % 1.0) == 0.0) {
                    return NaturalNumberConstant(exactSqrt.toInt().toBigInteger())
                }
                return this
            }
        }
        if (of is RealNumber) {
            return RealNumberConstant(sqrt(of.doubleValue))
        }

        return this
    }

    override fun represent(): String {
        return "sqrt(${of.represent()})"
    }

}

class Addition(private val left: NumericExpression, private val right: NumericExpression): NumericExpression {
    override fun calculate(exact: Boolean): NumericExpression {
        val left = left.calculate(exact)
        val right = right.calculate(exact)
        if (left is NaturalNumber) {
            if (right is NaturalNumber) {
                return NaturalNumberConstant(left.longValue + right.longValue)
            }
            if (right is RealNumber) {
                return RealNumberConstant(left.doubleValue + right.doubleValue)
            }
        }
        if (left is RealNumber && right is RealNumber) {
            return RealNumberConstant(left.doubleValue + right.doubleValue)
        }
        return this
    }

    override fun represent(): String {
        return "${left.represent()}+${right.represent()}"
    }

}

class Fraction(val up: NumericExpression, val low: NumericExpression): NumericExpression {
    override fun represent(): String {
        return "(${up.represent()}/${low.represent()})"
    }

    override fun calculate(exact: Boolean): NumericExpression {
        val up = up.calculate(exact)
        val low = low.calculate(exact)
        if (up is NaturalNumber && low is NaturalNumber) {
            // if dividable return Number
            val dividable = (up.longValue % low.longValue) == BigInteger.ZERO
            if (dividable) {
                return NaturalNumberConstant(up.longValue / low.longValue)
            }

            // try to simplify the fraction
            val ggt = ggt(up.longValue, low.longValue)
            if (ggt > BigInteger.ONE) {
                return Fraction(NaturalNumberConstant(up.longValue / ggt),
                    NaturalNumberConstant(low.longValue / ggt))
            }

            if (exact) {
                return RealNumberConstant(up.doubleValue / low.doubleValue)
            }
        }
        if (up is RealNumber && low is RealNumber) {
            return RealNumberConstant(up.doubleValue / low.doubleValue)
        }
        return this
    }

}

class Equals(val left: NumericExpression, val right: NumericExpression): BooleanExpression {
    override fun calculate(): BooleanExpression {
        return this
    }

    override fun represent(): String {
        return "$left = $right"
    }

}