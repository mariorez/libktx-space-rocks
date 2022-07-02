package system

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.quillraven.fleks.*
import component.*
import listener.ScoreListener

@AllOf([PlayerComponent::class])
class ShootingSystem(
    @Qualifier("laser") private val laser: Texture,
    private val score: ScoreListener,
    private val input: ComponentMapper<InputComponent>,
    private val playerMapper: ComponentMapper<PlayerComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    private var playerXCenter: Float? = null
    private var playerYCenter: Float? = null
    private val laserXCenter = laser.width / 2
    private val laserYCenter = laser.height / 2

    override fun onTickEntity(entity: Entity) {

        if (!input[entity].shoot || playerMapper[entity].ammunition <= 0) return
        input[entity].shoot = false
        playerMapper[entity].ammunition--
        score.ammunition = playerMapper[entity].ammunition

        if (playerXCenter == null) playerXCenter = render[entity].sprite.width / 2
        if (playerYCenter == null) playerYCenter = render[entity].sprite.height / 2

        world.entity {
            add<ShootComponent>()
            add<RenderComponent> { sprite = Sprite(laser) }
            add<FadeEffectComponent> {
                duration = 1.5f
                removeEntityOnEnd = true
            }
            add<TransformComponent> {
                position.x = transform[entity].position.x + playerXCenter!! - laserXCenter
                position.y = transform[entity].position.y + playerYCenter!! - laserYCenter
                zIndex = 1f
                setSpeed(400f)
                maxSpeed = 400f
                deceleration = 0f
                rotation = transform[entity].rotation
                setMotionAngle(transform[entity].rotation)
            }

        }
    }
}
