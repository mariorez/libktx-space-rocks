package component

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Polygon

data class RenderComponent(
    var sprite: Sprite = Sprite()
) {
    fun getPolygon(): Polygon {
        val polygon = Polygon().apply {
            vertices = floatArrayOf(
                sprite.x, sprite.y,
                sprite.x + sprite.width, sprite.y,
                sprite.x + sprite.width, sprite.y + sprite.height,
                sprite.x, sprite.y + sprite.height
            )
        }
        return polygon
    }
}
