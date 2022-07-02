import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import ktx.app.KtxGame
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import screen.GameScreen

class Main : KtxGame<KtxScreen>() {
    companion object {
        var gameSizes = GameSizes(
            windowWidth = 960,
            windowHeight = 540
        )
    }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        Gdx.input.inputProcessor = if (Platform.isMobile) InputMultiplexer()
        else InputMultiplexer(object : KtxInputAdapter {
            override fun keyDown(keycode: Int): Boolean {
                (currentScreen as BaseScreen).apply {
                    getActionMap()[keycode]?.let { doAction(Action(it, Action.Type.START)) }
                }
                return super.keyDown(keycode)
            }

            override fun keyUp(keycode: Int): Boolean {
                (currentScreen as BaseScreen).apply {
                    getActionMap()[keycode]?.let { doAction(Action(it, Action.Type.END)) }
                }
                return super.keyUp(keycode)
            }
        })

        KtxAsync.initiate()

        val assets = AssetStorage().apply {
            setLoader<FreeTypeFontGenerator> { FreeTypeFontGeneratorLoader(fileResolver) }
            setLoader<BitmapFont>(".ttf") { FreetypeFontLoader(fileResolver) }

            loadSync<BitmapFont>("open-sans.ttf", FreeTypeFontLoaderParameter().apply {
                fontFileName = "open-sans.ttf"
                fontParameters.apply {
                    size = 20
                    color = Color.WHITE
                    borderColor = Color.BLACK
                    borderWidth = 1f
                    borderStraight = true
                    minFilter = Texture.TextureFilter.Linear
                    magFilter = Texture.TextureFilter.Linear
                }
            })
            loadSync<Texture>("space.png").setFilter(Linear, Linear)
            loadSync<Texture>("spaceship.png").setFilter(Linear, Linear)
            loadSync<Texture>("rock.png").setFilter(Linear, Linear)
            loadSync<Texture>("laser.png").setFilter(Linear, Linear)
            loadSync<Texture>("shield.png").setFilter(Linear, Linear)
            loadSync<Texture>("warp.png").setFilter(Linear, Linear)
            loadSync<Texture>("reset.png").setFilter(Linear, Linear)
            if (Platform.isMobile) {
                loadSync<Texture>("touchpad-bg.png").setFilter(Linear, Linear)
                loadSync<Texture>("touchpad-knob.png").setFilter(Linear, Linear)
                loadSync<Texture>("button-laser.png").setFilter(Linear, Linear)
                loadSync<Texture>("button-turbo.png").setFilter(Linear, Linear)
                loadSync<Texture>("button-warp.png").setFilter(Linear, Linear)
            }
        }

        addScreen(GameScreen(assets))
        setScreen<GameScreen>()
    }
}
