package component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect

data class ParticleEffectComponent(
    var particle: ParticleEffect = ParticleEffect()
) {
    fun load(fileName: String) {
        particle.load(Gdx.files.internal(fileName), Gdx.files.internal("assets"))
    }
}