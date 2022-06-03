package system

import WorldSize
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import component.RenderComponent
import component.TransformComponent
import component.WrapAroundWorldComponent
import ktx.ashley.allOf

class WrapAroundWorldSystem(
    private val worldSize: WorldSize
) : IteratingSystem(
    allOf(WrapAroundWorldComponent::class, RenderComponent::class, TransformComponent::class).get()
) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        TransformComponent.mapper.get(entity).position.apply {
            RenderComponent.mapper.get(entity).apply {
                if (x + sprite.width < 0) x = worldSize.width.toFloat()
                if (x > worldSize.width) x = -sprite.width
                if (y + sprite.height < 0) y = worldSize.height.toFloat()
                if (y > worldSize.height) y = -sprite.height
            }
        }
    }
}
