package system

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.InputComponent
import component.ParticleEffectComponent
import component.TransformComponent
import ktx.app.Platform
import kotlin.properties.Delegates

class InputSystem(
    private val input: ComponentMapper<InputComponent>,
    private val transform: ComponentMapper<TransformComponent>,
    private val particle: ComponentMapper<ParticleEffectComponent>
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()
    var touchpad: Touchpad by Delegates.notNull()
    private val speedUp = Vector2()
    private val direction = Vector2()

    override fun onTick() {
        if (Platform.isMobile) {
            direction.set(touchpad.knobPercentX, touchpad.knobPercentY)
            transform[player].apply {
                if (direction.len() > 0) {
                    val degrees = degreesPerSecond * deltaTime
                    if (direction.angleDeg() in 90f..270f) {
                        rotateBy(degrees)
                    }
                    if (direction.angleDeg() in 0f..90f || direction.angleDeg() in 270f..360f) {
                        rotateBy(-degrees)
                    }
                }
                if (input[player].turbo) {
                    particle[player].particle.start()
                    speedUp.set(acceleration, 0f).also { speed ->
                        accelerator.add(speed).setAngleDeg(rotation)
                    }
                } else {
                    particle[player].particle.allowCompletion()
                }
            }
        } else {
            input[player].also { playerInput ->
                if (playerInput.isMoving) {
                    transform[player].apply {
                        speedUp.set(acceleration, 0f).also { speed ->
                            val degrees = degreesPerSecond * deltaTime
                            if (playerInput.left) rotateBy(degrees)
                            if (playerInput.right) rotateBy(-degrees)
                            if (playerInput.turbo) {
                                particle[player].particle.start()
                                accelerator.add(speed).setAngleDeg(rotation)
                            }
                        }
                    }
                } else {
                    particle[player].particle.allowCompletion()
                }
            }
        }
    }
}
