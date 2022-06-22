package system

import GameSizes
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
    private val gameSizes: GameSizes,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        transform[entity].apply {
            render[entity].apply {
                if (position.x + sprite.width < 0) position.x = gameSizes.worldWidthF()
                if (position.x > gameSizes.worldWidthF()) position.x = -sprite.width
                if (position.y + sprite.height < 0) position.y = gameSizes.windowHeightF()
                if (position.y > gameSizes.windowHeightF()) position.y = -sprite.height
            }
        }
    }
}
