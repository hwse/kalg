import java.lang.IllegalArgumentException
import java.math.BigInteger

fun Double.format(digits: Int): String = java.lang.String.format("%.${digits}f", this)

fun ggt(x: BigInteger, y:BigInteger): BigInteger {
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