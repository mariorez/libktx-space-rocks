package system

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.InputComponent
import component.ParticleEffectComponent
import component.PlayerComponent
import component.TransformComponent
import ktx.app.Platform
import kotlin.properties.Delegates

@AllOf([PlayerComponent::class])
class InputSystem(
    private val input: ComponentMapper<InputComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val particle: ComponentMapper<ParticleEffectComponent>
) : IteratingSystem() {

    var touchpad: Touchpad by Delegates.notNull()
    private val speedUp = Vector2()
    private val direction = Vector2()

    override fun onTickEntity(entity: Entity) {
        if (Platform.isMobile) {
            direction.set(touchpad.knobPercentX, touchpad.knobPercentY)
            transform[entity].apply {
                if (direction.len() > 0) {
                    val degrees = degreesPerSecond * deltaTime
                    if (direction.angleDeg() in 135f..270f) {
                        rotateBy(degrees)
                    }
                    if (direction.angleDeg() in 0f..45f || direction.angleDeg() in 270f..360f) {
                        rotateBy(-degrees)
                    }
                    if (direction.angleDeg() in 45f..135f) {
                        particle[entity].particle.start()
                        speedUp.set(acceleration, 0f).also { speed ->
                            accelerator.add(speed).setAngleDeg(rotation)
                        }
                    } else {
                        particle[entity].particle.allowCompletion()
                    }
                } else {
                    particle[entity].particle.allowCompletion()
                }
            }
        } else {
            input[entity].also { playerInput ->
                if (playerInput.isMoving) {
                    transform[entity].apply {
                        speedUp.set(acceleration, 0f).also { speed ->
                            val degrees = degreesPerSecond * deltaTime
                            if (playerInput.left) rotateBy(degrees)
                            if (playerInput.right) rotateBy(-degrees)
                            if (playerInput.turbo) {
                                particle[entity].particle.start()
                                accelerator.add(speed).setAngleDeg(rotation)
                            }
                        }
                    }
                }

                if (!input[entity].turbo) {
                    particle[entity].particle.allowCompletion()
                }
            }
        }
    }
}
