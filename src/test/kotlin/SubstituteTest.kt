import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SubstituteTest {

    @Test
    fun testEquals() {
        val vars = mapOf(Pair("x", 42.toExpression()))
        val result = Variable("x").substitute(vars)
        assertTrue(result is NaturalNumber)
        assertEquals(42.toBigInteger(), (result as NaturalNumber).longValue)

        val substituted = Addition(Variable("x"), Variable("x")).substitute(vars)
        val result2 = (substituted as NumericExpression).calculate()
        assertTrue(result2 is NaturalNumber)
        assertEquals(84.toBigInteger(), (result2 as NaturalNumber).longValue)
    }

}