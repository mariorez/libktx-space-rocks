package system

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector
import com.badlogic.gdx.math.Polygon
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Family
import com.github.quillraven.fleks.IteratingSystem
import component.ParticleEffectComponent
import component.RenderComponent
import component.RockComponent
import component.TransformComponent
import kotlin.properties.Delegates

@AllOf([RockComponent::class])
class CollisionSystem(
    private val render: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    var player: Entity by Delegates.notNull()
    var shoots: Family by Delegates.notNull()

    override fun onTickEntity(entity: Entity) {
        var noCollision = true
        val rockBox = render[entity].getPolygon()
        shoots.forEach { shootEntity ->
            if (noCollision) {
                render[shootEntity].getPolygon().also { shootBox ->
                    if (overlaps(shootBox, rockBox)) {
                        world.apply {
                            noCollision = false
                            remove(shootEntity)
                            remove(entity)
                            entity {
                                add<TransformComponent> { zIndex++ }
                                add<ParticleEffectComponent> {
                                    load("explosion.pfx")
                                    particle.setPosition(rockBox.x, rockBox.y)
                                    particle.start()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun overlaps(
        thisBox: Polygon,
        otherBox: Polygon,
        mtv: MinimumTranslationVector = MinimumTranslationVector()
    ): Boolean {
        // initial test to improve performance
        if (thisBox.boundingRectangle.overlaps(otherBox.boundingRectangle)) {
            return Intersector.overlapConvexPolygons(thisBox, otherBox, mtv)
        }
        return false
    }
}
