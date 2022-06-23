package component

import com.badlogic.gdx.graphics.g2d.ParticleEffect
import ktx.assets.toInternalFile

data class ParticleEffectComponent(
    var particle: ParticleEffect = ParticleEffect()
) {
    fun load(fileName: String): ParticleEffect {
        particle.load(fileName.toInternalFile(), fileName.toInternalFile().parent())
        return particle
    }

    fun rotateBy(degrees: Float) {
        for (emitter in particle.emitters) {
            val angle = emitter.angle
            val amplitude = (angle.highMax - angle.highMin) / 2f
            val h1 = degrees + amplitude
            val h2 = degrees - amplitude
            angle.setHigh(h1, h2)
            angle.setLow(degrees)
        }
    }
}