data class GameSizes(
    var windowWidth: Int,
    var windowHeight: Int,
    var worldWidth: Int = windowWidth,
    var worldHeight: Int = worldWidth
) {
    fun windowWidthF(): Float = windowWidth.toFloat()
    fun windowHeightF(): Float = windowHeight.toFloat()
    fun worldWidthF(): Float = worldWidth.toFloat()
    fun worldHeightF(): Float = worldHeight.toFloat()
}
