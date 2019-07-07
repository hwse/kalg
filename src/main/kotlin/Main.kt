fun main() {

    val y = Fraction(SquareRoot(NaturalNumberConstant(100.toBigInteger())), NaturalNumberConstant(4.toBigInteger()))
    println("${y.represent()} = ${y.calculate().represent()}")
}