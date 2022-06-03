package component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class RenderComponent : Component, Poolable {

    var sprite: Sprite = Sprite()

    override fun reset() {
        sprite = Sprite()
    }

    companion object {
        val mapper = mapperFor<RenderComponent>()
    }
}

