package component

data class InputComponent(
    var left: Boolean = false,
    var right: Boolean = false,
    var turbo: Boolean = false,
    var shoot: Boolean = false
) {
    val isMoving: Boolean get() = turbo || left || right
}