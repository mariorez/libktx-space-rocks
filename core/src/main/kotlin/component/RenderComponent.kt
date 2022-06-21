package component

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Polygon

data class RenderComponent(
    var sprite: Sprite = Sprite()
) {
    private var polygon: Polygon? = null

    fun getPolygon(): Polygon {
        polygon?.let {
            return it.apply {
                setPosition(sprite.x, sprite.y)
                setOrigin(sprite.originX, sprite.originY)
                rotation = sprite.rotation
                setScale(sprite.scaleX, sprite.scaleY)
            }
        }
        polygon = Polygon().apply {
            vertices = floatArrayOf(
                sprite.x, sprite.y,
                sprite.x + sprite.width, sprite.y,
                sprite.x + sprite.width, sprite.y + sprite.height,
                sprite.x, sprite.y + sprite.height
            )
        }
        return polygon as Polygon
    }
}
