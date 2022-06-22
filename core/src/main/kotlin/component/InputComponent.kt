package component

data class InputComponent(
    var up: Boolean = false,
    var left: Boolean = false,
    var right: Boolean = false,
    var shoot: Boolean = false
) {
    val isMoving: Boolean get() = up || left || right
}