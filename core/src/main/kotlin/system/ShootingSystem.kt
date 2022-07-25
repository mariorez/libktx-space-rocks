package system

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import component.FadeEffectComponent
import component.InputComponent
import component.PlayerComponent
import component.RenderComponent
import component.ShootComponent
import component.TransformComponent
import listener.ScoreListener

@AllOf([PlayerComponent::class])
class ShootingSystem(
    @Qualifier("laser") private val laser: Texture,
    private val score: ScoreListener,
    private val inputMap: ComponentMapper<InputComponent>,
    private val playerMap: ComponentMapper<PlayerComponent>,
    private val transformMap: ComponentMapper<TransformComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {

        if (!inputMap[entity].shoot || playerMap[entity].ammunition <= 0) return
        inputMap[entity].shoot = false
        playerMap[entity].ammunition--
        score.ammunition = playerMap[entity].ammunition

        val radius = 35f
        var shootX: Float
        var shootY: Float
        var shootRotation: Float

        transformMap[entity].apply {
            shootX = position.x + (radius * MathUtils.cosDeg(rotation))
            shootY = position.y + 28f + (radius * MathUtils.sinDeg(rotation))
            shootRotation = rotation
        }

        world.entity {
            add<ShootComponent>()
            add<RenderComponent> { sprite = Sprite(laser) }
            add<FadeEffectComponent> {
                duration = 1.5f
                removeEntityOnEnd = true
            }
            add<TransformComponent> {
                position.x = shootX
                position.y = shootY
                zIndex = 1f
                setSpeed(400f)
                maxSpeed = 400f
                deceleration = 0f
                rotation = shootRotation
                setMotionAngle(shootRotation)
            }
        }
    }
}
