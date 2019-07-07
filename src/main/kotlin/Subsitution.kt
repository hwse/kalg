
fun Expression.substitute(variables: Map<String, Expression>): Expression {
    return when (this) {
        is Variable -> variables[this.name]?: this
        is Parent -> reconstruct(child.substitute(variables))
        is Parent2 -> {
            val (child1, child2) = children
            reconstruct(child1.substitute(variables), child2.substitute(variables))
        }
        else -> this
    }
}