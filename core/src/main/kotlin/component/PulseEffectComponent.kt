package component

data class PulseEffectComponent(
    var scaleMode: Mode = Mode.IN,
    var minScale: Float = 0f,
    var maxScale: Float = 0f,
    var duration: Float = 1f
) {
    enum class Mode { IN, OUT }
}
