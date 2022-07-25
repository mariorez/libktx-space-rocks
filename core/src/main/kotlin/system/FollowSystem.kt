package system

import getCenterX
import getCenterY
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.FollowComponent
import component.RenderComponent
import component.TransformComponent

@AllOf([FollowComponent::class])
class FollowSystem(
    private val followMap: ComponentMapper<FollowComponent>,
    private val transformMap: ComponentMapper<TransformComponent>,
    private val renderMap: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {

        val targetEntity = followMap[entity].target as Entity

        if (!renderMap.contains(targetEntity)) {
            world.remove(entity)
            return
        }

        val followSprite = renderMap[entity].sprite
        val targetSprite = renderMap[targetEntity].sprite

        transformMap[entity].apply {
            if (followMap[entity].centralize) {
                position.x = targetSprite.getCenterX() - (followSprite.width / 2)
                position.y = targetSprite.getCenterY() - (followSprite.height / 2)
            } else {
                position.set(targetSprite.x, targetSprite.y)
            }

            if (followMap[entity].above) zIndex = transformMap[targetEntity].zIndex + 1
        }
    }
}
