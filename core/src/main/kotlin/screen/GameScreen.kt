package screen

import Action
import BaseScreen
import GameBoot
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import component.RenderComponent
import component.TransformComponent
import ktx.ashley.entity
import ktx.ashley.with
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import system.RenderingSystem

class GameScreen(
    private val assets: AssetStorage
) : BaseScreen() {

    private val engine = PooledEngine()
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera(
        GameBoot.WINDOW_WIDTH.toFloat(),
        GameBoot.WINDOW_HEIGHT.toFloat()
    ).apply { setToOrtho(false) }

    init {
        engine.apply {
            addSystem(RenderingSystem(batch, camera))

            entity {
                with<TransformComponent> { position.set(300f, 200f) }
                with<RenderComponent> {
                    sprite = Sprite(assets.get<Texture>("spaceship.png"))
                }
            }
        }
    }

    override fun doAction(action: Action) {
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun dispose() {
        batch.disposeSafely()
        assets.disposeSafely()
        uiStage.disposeSafely()
    }
}