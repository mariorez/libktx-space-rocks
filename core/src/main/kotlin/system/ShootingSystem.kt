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
    private val laser: Sprite,
    private val input: ComponentMapper<InputComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IntervalSystem() {

    private var player: Entity by Delegates.notNull()
    private var playerXCenter: Float by Delegates.notNull()
    private var playerYCenter: Float by Delegates.notNull()
    private val laserXCenter = laser.width / 2
    private val laserYCenter = laser.height / 2

    fun addPlayer(player: Entity) {
        this.player = player
        playerXCenter = render[player].sprite.width / 2
        playerYCenter = render[player].sprite.height / 2
    }

    override fun onTick() {
        if (input[player].shoot) {
            world.entity {
                add<ShootComponent>()
                add<RenderComponent> { sprite = laser }
                add<TransformComponent> {
                    position.x = transform[player].position.x + playerXCenter - laserXCenter
                    position.y = transform[player].position.y + playerYCenter - laserYCenter
                    zIndex = 2f
                    setSpeed(400f)
                    maxSpeed = 400f
                    deceleration = 0f
                    rotation = transform[player].rotation
                    setMotionAngle(transform[player].rotation)
                }
            }

            input[player].shoot = false
        }
    }
}
