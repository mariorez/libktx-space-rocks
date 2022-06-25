package system

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.FadeEffectComponent
import component.FadeEffectComponent.Mode.IN
import component.FadeEffectComponent.Mode.OUT
import component.RenderComponent

@AllOf([RenderComponent::class, FadeEffectComponent::class])
class FadeEffectSystem(
    private val renderMapper: ComponentMapper<RenderComponent>,
    private val fadeMapper: ComponentMapper<FadeEffectComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {

        val sprite = renderMapper[entity].sprite

        fadeMapper[entity].apply {

            val fadeAmount = deltaTime / duration

            when (mode) {
                IN -> {
                    alpha += fadeAmount
                    if (alpha >= 1f) cleanUp(entity, removeEntityOnEnd)
                }
                OUT -> {
                    alpha -= fadeAmount
                    if (alpha <= 0f) cleanUp(entity, removeEntityOnEnd)
                }
            }

            sprite.setAlpha(alpha)
        }
    }

    private fun cleanUp(entity: Entity, remove: Boolean) {
        configureEntity(entity) {
            fadeMapper.remove(entity)
        }
        if (remove) world.remove(entity)
    }
}
