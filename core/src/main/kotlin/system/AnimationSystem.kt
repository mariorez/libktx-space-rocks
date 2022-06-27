package system

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import component.AnimationComponent
import component.RenderComponent
import ktx.collections.gdxArrayOf

@AllOf([AnimationComponent::class])
class AnimationSystem(
    private val animationMapper: ComponentMapper<AnimationComponent>,
    private val renderMapper: ComponentMapper<RenderComponent>
) : IteratingSystem() {

    private val animationCache = mutableMapOf<TextureRegion, Animation<TextureRegion>>()

    override fun onTickEntity(entity: Entity) {
        val animation = animationMapper[entity].apply {
            stateTime += deltaTime
        }

        val animationFrame = getAnimation(animation).getKeyFrame(animation.stateTime)

        renderMapper[entity].sprite.apply {
            setRegion(animationFrame)
            setSize(animationFrame.regionWidth.toFloat(), animationFrame.regionHeight.toFloat())
        }
    }

    private fun getAnimation(animation: AnimationComponent): Animation<TextureRegion> {

        return animationCache.getOrPut(animation.region) {
            val frameWidth = animation.region.regionWidth / animation.cols
            val frameHeight = animation.region.regionHeight / animation.rows
            val regions = animation.region.split(frameWidth, frameHeight)

            val textureArray = gdxArrayOf<TextureRegion>().apply {
                (0 until animation.rows).forEach { row ->
                    (0 until animation.cols).forEach { col ->
                        add(TextureRegion(regions[row][col]))
                    }
                }
            }

            Animation(animation.frameDuration, textureArray).apply {
                playMode = animation.playMode
            }
        }.apply {
            playMode = animation.playMode
        }
    }
}
