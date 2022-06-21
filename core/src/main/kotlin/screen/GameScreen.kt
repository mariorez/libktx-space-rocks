package screen

import Action
import BaseScreen
import GameBoot.Companion.gameSizes
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import component.InputComponent
import component.PlayerComponent
import component.RenderComponent
import component.RockComponent
import component.TransformComponent
import component.WrapAroundWorldComponent
import generateButton
import ktx.actors.onTouchEvent
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import system.InputSystem
import system.MovementSystem
import system.RenderingSystem
import system.ShootingSystem
import system.WrapAroundWorldSystem
import kotlin.properties.Delegates
import kotlin.random.Random.Default.nextInt

class GameScreen(
    private val assets: AssetStorage
) : BaseScreen() {
    private var spaceship: Entity by Delegates.notNull()
    private var rocksQuantity = 10
    private lateinit var touchpad: Touchpad
    private val world = World {
        inject(batch)
        inject(camera)
        inject(gameSizes)
        inject(Sprite(assets.get<Texture>("laser.png")))
        system<InputSystem>()
        system<MovementSystem>()
        system<ShootingSystem>()
        system<WrapAroundWorldSystem>()
        system<RenderingSystem>()
    }

    init {
        spawnPlayer()
        spawnRocks()
        buildControls()

        world.apply {
            entity {
                add<TransformComponent>()
                add<RenderComponent> { sprite = Sprite(assets.get<Texture>("space.png")) }
            }

            // late injections
            system<ShootingSystem>().addPlayer(spaceship)
            system<InputSystem>().also {
                it.player = spaceship
                if (Platform.isMobile) it.touchpad = touchpad
            }
        }
    }

    override fun render(delta: Float) {
        world.update(delta)
        hudStage.draw()
    }

    override fun dispose() {
        super.dispose()
        world.dispose()
        assets.disposeSafely()
    }

    private fun buildControls() {
        if (Platform.isMobile) {
            touchpad = Touchpad(5f, Touchpad.TouchpadStyle().apply {
                background = TextureRegionDrawable(TextureRegion(TextureRegion(assets.get<Texture>("touchpad-bg.png"))))
                knob = TextureRegionDrawable(TextureRegion(assets.get<Texture>("touchpad-knob.png")))
            })

            val laser = generateButton(assets["button-laser.png"]).apply {
                onTouchEvent(
                    onDown = { _ -> doAction(Action(Action.Name.SHOOT, Action.Type.START)) },
                    onUp = { _ -> doAction(Action(Action.Name.SHOOT, Action.Type.END)) }
                )
            }

            hudStage.addActor(Table().apply {
                setFillParent(true)
                pad(5f)
                add(touchpad).expandY().expandX().left().bottom()
                add(laser).bottom()
            })
        } else {
            registerAction(Input.Keys.UP, Action.Name.UP)
            registerAction(Input.Keys.DOWN, Action.Name.DOWN)
            registerAction(Input.Keys.LEFT, Action.Name.LEFT)
            registerAction(Input.Keys.RIGHT, Action.Name.RIGHT)
            registerAction(Input.Keys.SPACE, Action.Name.SHOOT)
        }
    }

    private fun spawnPlayer() {
        spaceship = world.entity {
            add<PlayerComponent>()
            add<WrapAroundWorldComponent>()
            add<InputComponent>()
            add<TransformComponent> {
                position.set(gameSizes.windowWidthF() / 2, gameSizes.windowHeightF() / 2)
                zIndex += rocksQuantity + 2
                acceleration = 150f
                deceleration = 10f
                maxSpeed = 100f
                degreesPerSecond = 120f
            }
            add<RenderComponent> {
                sprite = Sprite(assets.get<Texture>("spaceship.png"))
            }
        }
    }

    private fun spawnRocks() {
        val rockImage = assets.get<Texture>("rock.png")
        repeat(rocksQuantity) { index ->
            world.entity {
                add<RockComponent>()
                add<WrapAroundWorldComponent>()
                add<RenderComponent> { sprite = Sprite(rockImage) }
                add<TransformComponent> {
                    position.x = nextInt(0, gameSizes.windowWidth - rockImage.width).toFloat()
                    position.y = nextInt(0, gameSizes.worldHeight - rockImage.height).toFloat()
                    zIndex += index + 1
                    acceleration = 50f
                    maxSpeed = 50f
                    setSpeed(50f)
                    setMotionAngle(nextInt(360).toFloat())
                }
            }
        }
    }

    override fun doAction(action: Action) {
        val input = world.mapper<InputComponent>()[spaceship]
        val isStarting = action.type == Action.Type.START
        when (action.name) {
            Action.Name.UP -> input.up = isStarting
            Action.Name.DOWN -> input.down = isStarting
            Action.Name.LEFT -> input.left = isStarting
            Action.Name.RIGHT -> input.right = isStarting
            Action.Name.SHOOT -> if (action.type == Action.Type.START) input.shoot = true
        }
    }
}