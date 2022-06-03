package system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.math.MathUtils
import component.TransformComponent

class PlayerMovementSystem(
    private val player: Entity
) : EntitySystem() {

    override fun update(deltaTime: Float) {
        TransformComponent.mapper.get(player).apply {
            // apply acceleration
            velocity.add(
                accelerator.x * deltaTime,
                accelerator.y * deltaTime
            )

            var speed = velocity.len()

            // decrease speed (decelerate) when not accelerating
            if (accelerator.len() == 0f) {
                speed -= deceleration * deltaTime
            }

            // keep speed within set bounds
            speed = MathUtils.clamp(speed, 0f, maxSpeed)

            // update velocity
            if (velocity.len() == 0f) {
                velocity.set(speed, 0f)
            } else {
                velocity.setLength(speed)
            }

            // move by
            if (velocity.x != 0f || velocity.y != 0f) {
                position.add(velocity.x * deltaTime, velocity.y * deltaTime)
            }

            // reset acceleration
            accelerator.set(0f, 0f)
        }
    }
}
