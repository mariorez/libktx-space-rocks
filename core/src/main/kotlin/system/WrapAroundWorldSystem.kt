package system

import GameBoot.Companion.sizes
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
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        transform[entity].apply {
            render[entity].apply {
                if (position.x + sprite.width < 0) position.x = sizes.worldWidthF()
                if (position.x > sizes.worldWidthF()) position.x = -sprite.width
                if (position.y + sprite.height < 0) position.y = sizes.windowHeightF()
                if (position.y > sizes.worldHeightF()) position.y = -sprite.height
            }
        }
    }
}
