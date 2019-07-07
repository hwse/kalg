import java.lang.IllegalArgumentException
import java.math.BigInteger
import javax.sound.sampled.BooleanControl

fun Double.format(digits: Int): String = java.lang.String.format("%.${digits}f", this)

fun Boolean.toExpression(): BoolConstant {
    return if (this) {
        BoolConstant.TRUE
    } else {
        BoolConstant.FALSE
    }
}

fun Int.toExpression(): NaturalNumberConstant {
    return NaturalNumberConstant(this.toBigInteger())
}

tailrec fun ggt(x: BigInteger, y:BigInteger): BigInteger {
    return if (y > BigInteger.ZERO) {
        ggt(y, x % y)
    } else {
        if (y == BigInteger.ZERO) {
                return x
        } else {
            throw IllegalArgumentException("y = $y (values below zero are not allowed)")
        }
    }
}