package system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.math.Vector2
import component.InputComponent
import component.TransformComponent

class InputSystem(
    private val player: Entity
) : EntitySystem() {

    private val playerInput = InputComponent.mapper.get(player)

    override fun update(deltaTime: Float) {
        TransformComponent.mapper.get(player).apply {
            val degrees = degreesPerSecond * deltaTime
            if (playerInput.left) rotation = rotateBy(rotation, degrees)
            if (playerInput.right) rotation = rotateBy(rotation, -degrees)
            if (playerInput.up) accelerator.add(Vector2(acceleration, 0f).setAngleDeg(rotation))
        }
    }

    private fun rotateBy(rotation: Float, degrees: Float): Float {
        return if (degrees != 0f) (rotation + degrees) % 360 else rotation
    }
}
