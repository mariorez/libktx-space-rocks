package component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class InputComponent : Component, Poolable {
    var up = false
    var left = false
    var right = false

    override fun reset() {
        up = false
        left = false
        right = false
    }

    companion object {
        val mapper = mapperFor<InputComponent>()
    }
}
