package system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.AnyOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.collection.compareEntity
import component.ParticleEffectComponent
import component.RenderComponent
import component.TransformComponent
import ktx.graphics.use

@AllOf([TransformComponent::class])
@AnyOf([RenderComponent::class, ParticleEffectComponent::class])
class RenderingSystem(
    private val batch: SpriteBatch,
    private val camera: OrthographicCamera,
    private val transform: ComponentMapper<TransformComponent>,
    private val render: ComponentMapper<RenderComponent>,
    private val particle: ComponentMapper<ParticleEffectComponent>
) : IteratingSystem(
    compareEntity { entA, entB -> transform[entA].zIndex.compareTo(transform[entB].zIndex) }
) {

    override fun onTick() {
        batch.use(camera) {
            super.onTick()
        }
    }

    override fun onTickEntity(entity: Entity) {
        if (particle.contains(entity)) {
            particle[entity].apply {
                if (render.contains(entity)) {
                    render[entity].sprite.also { sprite ->
                        particle.setPosition(
                            sprite.x + sprite.width / 2f,
                            sprite.y + sprite.height / 2f
                        )
                        rotateBy(sprite.rotation + 180)
                    }
                }
                particle.draw(batch)
            }
        }

        if (render.contains(entity)) {
            render[entity].apply {
                rendered = true
                sprite.apply {
                    transform[entity].also {
                        rotation = it.rotation
                        setBounds(it.position.x, it.position.y, width, height)
                    }
                    draw(batch)
                }
            }
        }
    }
}
