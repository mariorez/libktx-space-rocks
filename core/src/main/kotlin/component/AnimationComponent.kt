package component

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL
import com.badlogic.gdx.graphics.g2d.TextureRegion

data class AnimationComponent(
    var region: TextureRegion = TextureRegion(),
    var stateTime: Float = 0f,
    var playMode: PlayMode = NORMAL,
    var rows: Int = 1,
    var cols: Int = 1,
    var frameDuration: Float = 0f,
)
