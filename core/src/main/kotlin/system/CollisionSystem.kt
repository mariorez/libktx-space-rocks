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

    var player: Family by Delegates.notNull()
    var shoots: Family by Delegates.notNull()

    override fun onTickEntity(entity: Entity) {
        val rockSprite = render[entity].sprite
        val rockBox = render[entity].getPolygon()
        var noCollision = true
        shoots.forEach { shootEntity ->
            if (noCollision) {
                render[shootEntity].getPolygon().also { shootBox ->
                    if (overlaps(shootBox, rockBox)) {
                        noCollision = false
                        world.apply {
                            remove(shootEntity)
                            remove(entity)
                            entity {
                                add<TransformComponent> { zIndex++ }
                                add<ParticleEffectComponent> {
                                    load("explosion.pfx").apply {
                                        setPosition(
                                            rockSprite.x + rockSprite.width / 2,
                                            rockSprite.y + rockSprite.height / 2
                                        )
                                        start()
                                    }
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
