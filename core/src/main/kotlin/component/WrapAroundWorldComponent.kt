package component

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class WrapAroundWorldComponent : Component {
    companion object {
        val mapper = mapperFor<WrapAroundWorldComponent>()
    }
}
