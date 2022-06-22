import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import kotlin.math.max

data class GameSizes(
    var windowWidth: Int,
    var windowHeight: Int,
    var worldWidth: Int = windowWidth,
    var worldHeight: Int = worldWidth
) {
    fun windowWidthF(): Float = windowWidth.toFloat()
    fun windowHeightF(): Float = windowHeight.toFloat()
    fun worldWidthF(): Float = worldWidth.toFloat()
    fun worldHeightF(): Float = worldHeight.toFloat()
    fun unitsPerPixel(): Float = max(windowWidthF() / Gdx.graphics.width, windowHeightF() / Gdx.graphics.height)
}

fun generateButton(texture: Texture): Button {
    return Button(Button.ButtonStyle().apply {
        up = TextureRegionDrawable(texture)
    })
}
