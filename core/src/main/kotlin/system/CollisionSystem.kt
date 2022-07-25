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
import component.ShieldComponent
import component.TransformComponent
import getCenterX
import getCenterY
import listener.ScoreListener
import kotlin.properties.Delegates

@AllOf([RockComponent::class])
class CollisionSystem(
    private val score: ScoreListener,
    private val renderMap: ComponentMapper<RenderComponent>,
    private val shieldMap: ComponentMapper<ShieldComponent>
) : IteratingSystem() {

    var players: Family by Delegates.notNull()
    var shields: Family by Delegates.notNull()
    var shoots: Family by Delegates.notNull()

    override fun onTickEntity(entity: Entity) {
        val rockSprite = renderMap[entity].sprite
        val rockBox = renderMap[entity].getPolygon()

        players.firstOrNull()?.let { playerEntity ->
            renderMap[playerEntity].getPolygon(8).also { playerBox ->
                if (overlaps(playerBox, rockBox)) {
                    score.rocks--
                    explode(
                        renderMap[playerEntity].sprite.getCenterX(),
                        renderMap[playerEntity].sprite.getCenterY()
                    )
                    explode(
                        rockSprite.getCenterX(),
                        rockSprite.getCenterY()
                    )
                    world.remove(playerEntity)
                    world.remove(entity)
                }
            }
        }

        shields.forEach { shieldEntity ->
            renderMap[shieldEntity].getPolygon(8).also { shieldBox ->
                if (overlaps(shieldBox, rockBox)) {
                    shieldMap[shieldEntity].power -= 34f
                    score.rocks--
                    score.shieldPower = if (shieldMap[shieldEntity].power <= 0) 0f else shieldMap[shieldEntity].power
                    renderMap[shieldEntity].sprite.setAlpha(shieldMap[shieldEntity].power / 100f)
                    if (shieldMap[shieldEntity].power <= 0f) {
                        world.remove(shieldEntity)
                    }
                    explode(
                        rockSprite.getCenterX(),
                        rockSprite.getCenterY()
                    )
                    world.remove(entity)
                }
            }
        }

        var noLaseCollision = true
        shoots.forEach { shootEntity ->
            if (noLaseCollision) {
                renderMap[shootEntity].getPolygon().also { shootBox ->
                    if (overlaps(shootBox, rockBox)) {
                        noLaseCollision = false
                        score.rocks--
                        world.apply {
                            remove(shootEntity)
                            remove(entity)
                        }
                        explode(
                            rockSprite.getCenterX(),
                            rockSprite.getCenterY()
                        )
                    }
                }
            }
        }
    }

    private fun explode(x: Float, y: Float) {
        world.entity {
            add<TransformComponent> { zIndex++ }
            add<ParticleEffectComponent> {
                load("explosion.pfx").apply {
                    setPosition(x, y)
                    start()
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
