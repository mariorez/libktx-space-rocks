package system

import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.InputComponent
import component.RenderComponent
import component.ShootComponent
import component.TransformComponent
import kotlin.properties.Delegates

class ShootingSystem(
    private val input: ComponentMapper<InputComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val laser: Sprite
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()

    override fun onTick() {
        if (input[player].shoot) {
            world.entity {
                add<ShootComponent>()
                add<RenderComponent> { sprite = laser }
                add<TransformComponent> {
                    position.x = transform[player].position.x
                    position.y = transform[player].position.y
                    zIndex = 2f
                    setSpeed(400f)
                    maxSpeed = 400f
                    deceleration = 0f
                    rotation = transform[player].rotation
                    setMotionAngle(transform[player].getMotionAngle())
                }
            }

            input[player].shoot = false
        }
    }
}
