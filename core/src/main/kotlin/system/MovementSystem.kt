package system

import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.fleks.AnyOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.PlayerComponent
import component.RockComponent
import component.ShootComponent
import component.TransformComponent

@AnyOf([PlayerComponent::class, RockComponent::class, ShootComponent::class])
class MovementSystem(
    private val transform: ComponentMapper<TransformComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        transform[entity].apply {
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
            setSpeed(speed)

            // move by
            if (velocity.x != 0f || velocity.y != 0f) {
                position.add(velocity.x * deltaTime, velocity.y * deltaTime)
            }

            // reset acceleration
            accelerator.set(0f, 0f)
        }
    }
}
