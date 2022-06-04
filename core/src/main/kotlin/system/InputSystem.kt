package system

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IntervalSystem
import component.InputComponent
import component.TransformComponent
import kotlin.properties.Delegates

class InputSystem(
    private val input: ComponentMapper<InputComponent>,
    private val transform: ComponentMapper<TransformComponent>
) : IntervalSystem() {

    var player: Entity by Delegates.notNull()

    override fun onTick() {
        transform[player].apply {
            val degrees = degreesPerSecond * deltaTime
            if (input[player].left) rotateBy(degrees)
            if (input[player].right) rotateBy(-degrees)
            if (input[player].up) accelerator.add(Vector2(acceleration, 0f).setAngleDeg(rotation))
        }
    }
}
