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
import component.ParticleEffectComponent
import component.PlayerComponent
import component.RenderComponent
import component.RockComponent
import component.ShootComponent
import component.TransformComponent
import component.WrapAroundWorldComponent
import generateButton
import ktx.actors.onTouchEvent
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import system.CollisionSystem
import system.FadeEffectSystem
import system.InputSystem
import system.MovementSystem
import system.ParticleEffectSystem
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
        inject(assets.get<Texture>("laser.png"))
        system<MovementSystem>()
        system<ShootingSystem>()
        system<WrapAroundWorldSystem>()
        system<CollisionSystem>()
        system<FadeEffectSystem>()
        system<ParticleEffectSystem>()
        system<RenderingSystem>()
        system<InputSystem>()
    }

    init {
        spawnPlayer()
        spawnRocks()
        buildControls()

        world.apply {
            entity {
                add<TransformComponent> { zIndex -= 1 }
                add<RenderComponent> { sprite = Sprite(assets.get<Texture>("space.png")) }
            }

            // late injections
            system<CollisionSystem>().also {
                it.player = family(allOf = arrayOf(PlayerComponent::class))
                it.shoots = family(allOf = arrayOf(ShootComponent::class))
            }
            system<InputSystem>().also {
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

    private fun spawnPlayer() {
        spaceship = world.entity {
            add<PlayerComponent>()
            add<WrapAroundWorldComponent>()
            add<InputComponent>()
            add<TransformComponent> {
                position.set(gameSizes.windowWidthF() / 2, gameSizes.windowHeightF() / 2)
                zIndex += rocksQuantity + 1
                acceleration = 150f
                deceleration = 10f
                maxSpeed = 100f
                degreesPerSecond = 120f
            }
            add<RenderComponent> {
                sprite = Sprite(assets.get<Texture>("spaceship.png"))
            }
            add<ParticleEffectComponent> {
                load("thruster.pfx").apply { scaleEffect(0.35f) }
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
                    zIndex += index
                    acceleration = 50f
                    maxSpeed = 50f
                    setSpeed(50f)
                    setMotionAngle(nextInt(360).toFloat())
                }
            }
        }
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

            val turbo = generateButton(assets["button-turbo.png"]).apply {
                onTouchEvent(
                    onDown = { _ -> doAction(Action(Action.Name.TURBO, Action.Type.START)) },
                    onUp = { _ -> doAction(Action(Action.Name.TURBO, Action.Type.END)) }
                )
            }

            hudStage.addActor(Table().apply {
                setFillParent(true)
                pad(5f)
                add(touchpad).expandY().expandX().left().bottom()
                add(turbo).padRight(10f).bottom()
                add(laser).bottom()
            })
        } else {
            registerAction(Input.Keys.LEFT, Action.Name.LEFT)
            registerAction(Input.Keys.RIGHT, Action.Name.RIGHT)
            registerAction(Input.Keys.UP, Action.Name.TURBO)
            registerAction(Input.Keys.SPACE, Action.Name.SHOOT)
        }
    }

    override fun doAction(action: Action) {
        val mapper = world.mapper<InputComponent>()
        if (!mapper.contains(spaceship)) return
        val input = mapper[spaceship]
        val isStarting = action.type == Action.Type.START
        when (action.name) {
            Action.Name.LEFT -> input.left = isStarting
            Action.Name.RIGHT -> input.right = isStarting
            Action.Name.TURBO -> input.turbo = isStarting
            Action.Name.SHOOT -> if (action.type == Action.Type.START) input.shoot = true
        }
    }
}