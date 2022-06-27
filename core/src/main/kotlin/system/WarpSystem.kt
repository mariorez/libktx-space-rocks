package system

import GameBoot.Companion.gameSizes
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils.random
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import component.AnimationComponent
import component.FadeEffectComponent
import component.InputComponent
import component.PlayerComponent
import component.RenderComponent
import component.TransformComponent

@AllOf([PlayerComponent::class])
class WarpSystem(
    @Qualifier("warp") private val warpTexture: Texture,
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

        warpEffect(currentX, currentY, transformMapper[entity].zIndex + 1)
        warpEffect(newX, newY, transformMapper[entity].zIndex + 1)
    }

    private fun warpEffect(x: Float, y: Float, z: Float) {
        world.entity {
            add<RenderComponent>()
            add<TransformComponent> {
                position.set(x, y)
                zIndex = z
            }
            add<FadeEffectComponent> {
                delay = 1f
                duration = 0.5f
                removeEntityOnEnd = true
            }
            add<AnimationComponent> {
                region = TextureRegion(warpTexture)
                rows = 4
                cols = 8
                frameDuration = 0.05f
                playMode = Animation.PlayMode.LOOP
            }
        }
    }
}
