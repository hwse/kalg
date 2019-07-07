import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BooleanTest {

    @Test
    fun testEquals() {
        assertEquals(BoolConstant.TRUE,
            Equals(NaturalNumberConstant(1.toBigInteger()), RealNumberConstant(1.0)).calculate())

        assertEquals(BoolConstant.FALSE,
            Equals(NaturalNumberConstant(2.toBigInteger()), RealNumberConstant(2.1)).calculate())
    }

}