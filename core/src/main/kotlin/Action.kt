data class Action(
    val name: Name,
    val type: Type
) {
    enum class Name { LEFT, RIGHT, TURBO, SHOOT, WARP }
    enum class Type { START, END }
}
