package component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.ashley.mapperFor

class TransformComponent : Component, Poolable, Comparable<TransformComponent> {

    val position = Vector2()
    var zIndex = 0f
    var rotation = 0f
    val velocity = Vector2()
    val accelerator = Vector2()
    var acceleration = 0f
    var deceleration = 0f
    var maxSpeed = 0f

    override fun reset() {
        position.set(0f, 0f)
        zIndex = 0f
        rotation = 0f
        velocity.set(0f, 0f)
        acceleration = 0f
        deceleration = 0f
        maxSpeed = 0f
    }

    override fun compareTo(other: TransformComponent): Int {
        return other.zIndex.compareTo(zIndex)
    }

    companion object {
        val mapper = mapperFor<TransformComponent>()
    }
}
