package system

import WorldSize
import com.github.quillraven.fleks.AnyOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.PlayerComponent
import component.RenderComponent
import component.RockComponent
import component.TransformComponent

@AnyOf([PlayerComponent::class, RockComponent::class])
class WrapAroundWorldSystem(
    private val worldSize: WorldSize,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        transform[entity].position.apply {
            render[entity].apply {
                if (x + sprite.width < 0) x = worldSize.width.toFloat()
                if (x > worldSize.width) x = -sprite.width
                if (y + sprite.height < 0) y = worldSize.height.toFloat()
                if (y > worldSize.height) y = -sprite.height
            }
        }
    }
}
