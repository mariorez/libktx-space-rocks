@file:JvmName("Lwjgl3Launcher")

package mariorez.spacerocks.lwjgl3

import GameBoot
import GameBoot.Companion.WINDOW_HEIGHT
import GameBoot.Companion.WINDOW_WIDTH
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(GameBoot(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("SpaceRocks")
        setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
