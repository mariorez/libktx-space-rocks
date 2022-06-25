package system

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.FollowComponent
import component.RenderComponent
import component.TransformComponent

@AllOf([FollowComponent::class])
class FollowSystem(
    private val follow: ComponentMapper<FollowComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {

        val targetEntity = follow[entity].target as Entity
        if (!render.contains(targetEntity)) return

        val targetSprite = render[targetEntity].sprite
        val followSprite = render[entity].sprite

        transform[entity].apply {
            if (follow[entity].centralize) {
                position.x = (targetSprite.x + targetSprite.width / 2) - (followSprite.width / 2)
                position.y = (targetSprite.y + targetSprite.height / 2) - (followSprite.height / 2)
            } else {
                position.set(targetSprite.x, targetSprite.y)
            }
            if (follow[entity].above) zIndex = transform[targetEntity].zIndex + 1
        }
    }
}
