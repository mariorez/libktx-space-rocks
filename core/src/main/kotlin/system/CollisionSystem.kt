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
import listener.ScoreListener
import kotlin.properties.Delegates

@AllOf([RockComponent::class])
class CollisionSystem(
    private val score: ScoreListener,
    private val renderMapper: ComponentMapper<RenderComponent>,
    private val shieldMapper: ComponentMapper<ShieldComponent>
) : IteratingSystem() {

    var players: Family by Delegates.notNull()
    var shields: Family by Delegates.notNull()
    var shoots: Family by Delegates.notNull()

    override fun onTickEntity(entity: Entity) {
        val rockSprite = renderMapper[entity].sprite
        val rockBox = renderMapper[entity].getPolygon()

        players.forEach { playerEntity ->
            renderMapper[playerEntity].getPolygon(8).also { playerBox ->
                if (renderMapper[playerEntity].rendered && overlaps(playerBox, rockBox)) {
                    explode(
                        renderMapper[playerEntity].sprite.x + renderMapper[playerEntity].sprite.width / 2,
                        renderMapper[playerEntity].sprite.y + renderMapper[playerEntity].sprite.height / 2
                    )
                    explode(
                        rockSprite.x + rockSprite.width / 2,
                        rockSprite.y + rockSprite.height / 2
                    )
                    world.remove(playerEntity)
                    world.remove(entity)
                }
            }
        }

        shields.forEach { shieldEntity ->
            renderMapper[shieldEntity].getPolygon(8).also { shieldBox ->
                if (renderMapper[shieldEntity].rendered && overlaps(shieldBox, rockBox)) {
                    shieldMapper[shieldEntity].power -= 34f
                    score.rocks--
                    score.shieldPower = if (shieldMapper[shieldEntity].power <= 0) 0f else shieldMapper[shieldEntity].power
                    renderMapper[shieldEntity].sprite.setAlpha(shieldMapper[shieldEntity].power / 100f)
                    if (shieldMapper[shieldEntity].power <= 0f) {
                        world.remove(shieldEntity)
                    }
                    explode(
                        rockSprite.x + rockSprite.width / 2,
                        rockSprite.y + rockSprite.height / 2
                    )
                    world.remove(entity)
                }
            }
        }

        var noLaseCollision = true
        shoots.forEach { shootEntity ->
            if (noLaseCollision) {
                renderMapper[shootEntity].getPolygon().also { shootBox ->
                    if (renderMapper[shootEntity].rendered && overlaps(shootBox, rockBox)) {
                        noLaseCollision = false
                        score.rocks--
                        world.apply {
                            remove(shootEntity)
                            remove(entity)
                        }
                        explode(
                            rockSprite.x + rockSprite.width / 2,
                            rockSprite.y + rockSprite.height / 2
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
