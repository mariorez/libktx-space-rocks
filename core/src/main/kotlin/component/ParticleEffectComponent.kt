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
}