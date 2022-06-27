package system

import GameBoot.Companion.gameSizes
import com.badlogic.gdx.math.MathUtils.random
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.InputComponent
import component.PlayerComponent
import component.TransformComponent

@AllOf([PlayerComponent::class])
class WarpSystem(
    private val inputMapper: ComponentMapper<InputComponent>,
    private val transformMapper: ComponentMapper<TransformComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {

        if (!inputMapper[entity].warp) return
        inputMapper[entity].warp = false

        val currentX = transformMapper[entity].position.x
        val currentY = transformMapper[entity].position.y


        val newX = random(gameSizes.worldWidth).toFloat()
        val newY = random(gameSizes.worldHeight).toFloat()

        transformMapper[entity].position.set(newX, newY)

        world.entity {

        }
    }
}