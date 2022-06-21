data class Action(
    val name: Name,
    val type: Type
) {
    enum class Name { UP, DOWN, LEFT, RIGHT, SHOOT }
    enum class Type { START, END }
}
