import Action.Type.END
import Action.Type.START
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import ktx.app.KtxGame
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import screen.GameScreen

class GameBoot : KtxGame<KtxScreen>() {
    companion object {
        const val WINDOW_WIDTH = 800
        const val WINDOW_HEIGHT = 600
    }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        Gdx.input.inputProcessor = InputMultiplexer(object : KtxInputAdapter {
            override fun keyDown(keycode: Int): Boolean {
                (currentScreen as BaseScreen).apply {
                    getActionMap()[keycode]?.let { doAction(Action(it, START)) }
                }
                return super.keyDown(keycode)
            }

            override fun keyUp(keycode: Int): Boolean {
                (currentScreen as BaseScreen).apply {
                    getActionMap()[keycode]?.let { doAction(Action(it, END)) }
                }
                return super.keyUp(keycode)
            }
        })

        KtxAsync.initiate()

        val assets = AssetStorage().apply {
            loadSync<Texture>("spaceship.png").setFilter(Linear, Linear)
        }

        addScreen(GameScreen(assets))
        setScreen<GameScreen>()
    }
}