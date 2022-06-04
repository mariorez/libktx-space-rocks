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
        transform[entity].apply {
            render[entity].apply {
                if (position.x + sprite.width < 0) {
                    position.x = worldSize.width.toFloat()
                    position.y = if (getMotionAngle() in 170.0..190.0) position.y
                    else worldSize.height - position.y
                }

                if (position.x > worldSize.width) {
                    position.x = -sprite.width
                    position.y = if (getMotionAngle() <= 10 || getMotionAngle() >= 350) position.y
                    else worldSize.height - position.y
                }

                if (position.y + sprite.height < 0) {
                    position.y = worldSize.height.toFloat()
                    position.x = if (getMotionAngle() in 260.0..280.0) position.x
                    else worldSize.width - position.x
                }

                if (position.y > worldSize.height) {
                    position.y = -sprite.height
                    position.x = if (getMotionAngle() in 80.0..100.0) position.x
                    else worldSize.width - position.x
                }
            }
        }
    }
}
