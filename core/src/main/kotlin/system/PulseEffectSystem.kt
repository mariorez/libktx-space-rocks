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

            if (sprite.scaleX < maxScale && scaleMode == IN) {
                sprite.scale(scaleAmount)
            } else {
                scaleMode = OUT
            }

            if (sprite.scaleX > minScale && scaleMode == OUT) {
                sprite.scale(-scaleAmount)
            } else {
                scaleMode = IN
            }
        }
    }
}
