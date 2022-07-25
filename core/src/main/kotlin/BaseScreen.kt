import GameBoot.Companion.sizes
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

abstract class BaseScreen : KtxScreen {

    private val actionMap = mutableMapOf<Int, Action.Name>()
    protected val batch = SpriteBatch()
    protected val camera = OrthographicCamera().apply {
        setToOrtho(false, sizes.windowWidthF(), sizes.windowHeightF())
    }
    protected val hudStage = Stage(ScreenViewport().apply { unitsPerPixel = sizes.unitsPerPixel() })

    fun registerAction(inputKey: Int, actionName: Action.Name) {
        actionMap[inputKey] = actionName
    }

    fun getActionMap(): MutableMap<Int, Action.Name> = actionMap

    abstract fun doAction(action: Action)

    override fun show() {
        (Gdx.input.inputProcessor as InputMultiplexer).apply {
            addProcessor(hudStage)
        }
    }

    override fun hide() {
        (Gdx.input.inputProcessor as InputMultiplexer).apply {
            removeProcessor(hudStage)
        }
    }

    override fun dispose() {
        hudStage.disposeSafely()
        batch.disposeSafely()
    }
}
