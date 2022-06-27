package component

data class FadeEffectComponent(
    var mode: Mode = Mode.OUT,
    var alpha: Float = 1f,
    var duration: Float = 1f,
    var delay: Float = 0f,
    var removeEntityOnEnd: Boolean = false
) {
    enum class Mode { IN, OUT }
}
