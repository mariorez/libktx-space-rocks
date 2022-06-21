package system

import com.badlogic.gdx.math.Intersector.overlaps
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Family
import com.github.quillraven.fleks.IteratingSystem
import component.RenderComponent
import component.RockComponent
import component.TransformComponent
import kotlin.properties.Delegates

@AllOf([RockComponent::class])
class CollisionSystem(
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>,
) : IteratingSystem() {

    var player: Entity by Delegates.notNull()
    var shoots: Family by Delegates.notNull()

    override fun onTickEntity(entity: Entity) {

        var noCollision = true
        val rockBox = render[entity].sprite.boundingRectangle

        shoots.forEach { shootEntity ->
            if (noCollision) {
                render[shootEntity].sprite.also { shootSprite ->
                    if (overlaps(shootSprite.boundingRectangle, rockBox)) {
                        world.remove(shootEntity)
                        world.remove(entity)
                        noCollision = false
                    }
                }
            }
        }
    }
}

