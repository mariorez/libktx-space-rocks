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
            particle[entity].particle.apply {
                draw(batch)
            }
        } else {
            render[entity].sprite.apply {
                setOriginCenter()
                transform[entity].also {
                    rotation = it.rotation
                    setBounds(it.position.x, it.position.y, width, height)
                }
                draw(batch)
            }
        }
    }
}
