import GameBoot.Companion.gameSizes
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxScreen

abstract class BaseScreen : KtxScreen {

    private val actionMap = mutableMapOf<Int, Action.Name>()
    protected val batch = SpriteBatch()
    protected val camera = OrthographicCamera().apply {
        setToOrtho(false, gameSizes.windowWidthF(), gameSizes.windowHeightF())
    }

    fun registerAction(inputKey: Int, actionName: Action.Name) {
        actionMap[inputKey] = actionName
    }

    fun getActionMap(): MutableMap<Int, Action.Name> = actionMap

    abstract fun doAction(action: Action)
}
