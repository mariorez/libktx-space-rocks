package listener

data class ScoreListener(
    var rocks: Int = 0,
    var ammunition: Int = 0,
    var shieldPower: Float = 0f
) {

    fun reset(rocks: Int, ammunition: Int, shieldPower: Float) {
        this.rocks = rocks
        this.ammunition = ammunition
        this.shieldPower = shieldPower
    }

    fun print(): String {
        return "Rocks: $rocks | Shoots: $ammunition | Shield power: $shieldPower"
    }
}
