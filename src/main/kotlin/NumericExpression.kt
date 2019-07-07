import java.math.BigInteger
import kotlin.math.sqrt

interface Expression {
    fun represent(): String
}

interface Parent {
    /** get child -> go down the tree */
    val child: Expression

    /** rebuild this with other child */
    fun reconstruct(newChild: Expression): Expression
}

interface Parent2 {
    val children: Pair<Expression, Expression>
    fun reconstruct(newChild1: Expression, newChild2: Expression): Expression
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

    companion object {
        val FALSE = BoolConstant(false)
        val TRUE = BoolConstant(true)
    }

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

class Variable(val name: String): NumericExpression {

    override fun calculate(exact: Boolean): NumericExpression {
        return this
    }

    override fun represent(): String {
        return name
    }
}

class SquareRoot(private val of: NumericExpression): NumericExpression, Parent {

    override val child: Expression
        get() = of

    override fun reconstruct(newChild: Expression): Expression {
        return SquareRoot(newChild as NumericExpression)
    }

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

class Addition(private val left: NumericExpression, private val right: NumericExpression): NumericExpression, Parent2 {

    override val children: Pair<Expression, Expression>
        get() = Pair(left, right)

    override fun reconstruct(newChild1: Expression, newChild2: Expression): Expression {
        return Addition(newChild1 as NumericExpression, newChild2 as NumericExpression)
    }

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

class Fraction(val up: NumericExpression, val low: NumericExpression): NumericExpression, Parent2 {

    override val children: Pair<Expression, Expression>
        get() = Pair(up, low)

    override fun reconstruct(newChild1: Expression, newChild2: Expression): Expression {
        return Fraction(newChild1 as NumericExpression, newChild2 as NumericExpression)
    }

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

class Equals(val left: NumericExpression, val right: NumericExpression): BooleanExpression, Parent2 {

    override val children: Pair<Expression, Expression>
        get() = Pair(left, right)

    override fun reconstruct(newChild1: Expression, newChild2: Expression): Expression {
        return Equals(newChild1 as NumericExpression, newChild2 as NumericExpression)
    }

    override fun calculate(): BooleanExpression {
        val left = left.calculate(true)
        val right = right.calculate(true)

        if (left is RealNumber && right is RealNumber) {
            return (left.doubleValue == right.doubleValue).toExpression()
        }
        return this
    }

    override fun represent(): String {
        return "$left = $right"
    }

}

