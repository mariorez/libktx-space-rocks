package system

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.PulseEffectComponent
import component.PulseEffectComponent.Mode.IN
import component.PulseEffectComponent.Mode.OUT
import component.RenderComponent

@AllOf([RenderComponent::class, PulseEffectComponent::class])
class PulseEffectSystem(
    private val renderMapper: ComponentMapper<RenderComponent>,
    private val pulseMapper: ComponentMapper<PulseEffectComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {

        val sprite = renderMapper[entity].sprite

        pulseMapper[entity].apply {

            val scaleAmount = deltaTime / duration

            when (scaleMode) {
                IN -> {
                    if (sprite.scaleX < maxScale) sprite.scale(scaleAmount)
                    else scaleMode = OUT
                }
                OUT -> {
                    if (sprite.scaleX > minScale) sprite.scale(-scaleAmount)
                    else scaleMode = IN
                }
            }
        }
    }
}
