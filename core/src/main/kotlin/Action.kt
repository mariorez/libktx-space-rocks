data class Action(
    val name: Name,
    val type: Type
) {
    enum class Name { LEFT, RIGHT, TURBO, SHOOT }
    enum class Type { START, END }
}
