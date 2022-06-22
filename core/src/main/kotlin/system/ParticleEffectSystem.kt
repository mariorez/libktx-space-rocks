package system

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.ParticleEffectComponent

@AllOf([ParticleEffectComponent::class])
class ParticleEffectSystem(
    private val particle: ComponentMapper<ParticleEffectComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        particle[entity].particle.apply {
            update(deltaTime)
            if (isComplete && !emitters.first().isContinuous) {
                world.remove(entity)
                dispose()
            }
        }
    }
}
