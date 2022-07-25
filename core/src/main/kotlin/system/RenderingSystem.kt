package system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.AnyOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.collection.compareEntity
import component.ParticleEffectComponent
import component.RenderComponent
import component.TransformComponent
import getCenterX
import getCenterY
import ktx.graphics.use

@AllOf([TransformComponent::class])
@AnyOf([RenderComponent::class, ParticleEffectComponent::class])
class RenderingSystem(
    private val batch: SpriteBatch,
    private val camera: OrthographicCamera,
    private val transformMap: ComponentMapper<TransformComponent>,
    private val renderMap: ComponentMapper<RenderComponent>,
    private val particleMap: ComponentMapper<ParticleEffectComponent>
) : IteratingSystem(
    compareEntity { entA, entB -> transformMap[entA].zIndex.compareTo(transformMap[entB].zIndex) }
) {

    override fun onTick() {
        batch.use(camera) {
            super.onTick()
        }
    }

    override fun onTickEntity(entity: Entity) {
        if (particleMap.contains(entity)) {
            particleMap[entity].apply {
                if (renderMap.contains(entity)) {
                    renderMap[entity].sprite.also { sprite ->
                        val radius = 24f
                        val posX = radius * MathUtils.cosDeg(sprite.rotation)
                        val posY = radius * MathUtils.sinDeg(sprite.rotation)
                        particle.setPosition(
                            sprite.getCenterX() - posX,
                            sprite.getCenterY() - posY
                        )
                        rotateBy(sprite.rotation + 180)
                    }
                }
                particle.draw(batch)
            }
        }

        if (renderMap.contains(entity)) {
            renderMap[entity].apply {
                sprite.apply {
                    transformMap[entity].also {
                        rotation = it.rotation
                        setBounds(it.position.x, it.position.y, width, height)
                    }
                    draw(batch)
                }
            }
        }
    }
}
