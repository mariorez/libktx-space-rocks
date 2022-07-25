import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

fun Sprite.getCenterX(): Float = this.originX + this.x
fun Sprite.getCenterY(): Float = this.originY + this.y
fun Sprite.getCenter(): Vector2 = Vector2(this.getCenterX(), this.getCenterY())

fun generateButton(texture: Texture): Button {
    return Button(Button.ButtonStyle().apply {
        up = TextureRegionDrawable(texture)
    })
}
