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
import component.FollowComponent
import component.InputComponent
import component.ParticleEffectComponent
import component.PlayerComponent
import component.RenderComponent
import component.RockComponent
import component.ShieldComponent
import component.ShootComponent
import component.TransformComponent
import component.WrapAroundWorldComponent
import generateButton
import ktx.actors.onTouchDown
import ktx.actors.onTouchEvent
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import system.CollisionSystem
import system.FadeEffectSystem
import system.FollowSystem
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
        system<FollowSystem>()
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
        spawnShield()
        spawnRocks()
        buildControls()

        world.apply {
            entity {
                add<TransformComponent> { zIndex -= 1 }
                add<RenderComponent> { sprite = Sprite(assets.get<Texture>("space.png")) }
            }

            // late injections
            system<CollisionSystem>().also {
                it.players = family(allOf = arrayOf(PlayerComponent::class))
                it.shields = family(allOf = arrayOf(ShieldComponent::class))
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

    private fun spawnShield() {
        world.entity {
            add<ShieldComponent> { power = 100f }
            add<TransformComponent>()
            add<FollowComponent> {
                target = spaceship
                centralize = true
                above = true
            }
            add<RenderComponent> { sprite = Sprite(assets.get<Texture>("shield.png")) }
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

    private fun restart() {
        world.family(anyOf = arrayOf(PlayerComponent::class, ShieldComponent::class, RockComponent::class))
            .forEach { world.remove(it) }
        spawnPlayer()
        spawnShield()
        spawnRocks()
    }

    private fun buildControls() {
        val reset = generateButton(assets["reset.png"]).apply {
            onTouchDown { restart() }
        }

        val table = Table().apply {
            setFillParent(true)
            pad(5f)
            add(reset).colspan(3).expandY().expandX().top().right()
        }

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

            table.apply {
                row()
                add(touchpad).expandY().expandX().left().bottom()
                add(turbo).padRight(10f).bottom()
                add(laser).bottom()
            }
        } else {
            registerAction(Input.Keys.LEFT, Action.Name.LEFT)
            registerAction(Input.Keys.RIGHT, Action.Name.RIGHT)
            registerAction(Input.Keys.UP, Action.Name.TURBO)
            registerAction(Input.Keys.SPACE, Action.Name.SHOOT)
        }

        hudStage.addActor(table)
    }

    override fun doAction(action: Action) {
        val input = world.mapper<InputComponent>()
        if (!input.contains(spaceship)) return
        val isStarting = action.type == Action.Type.START
        when (action.name) {
            Action.Name.LEFT -> input[spaceship].left = isStarting
            Action.Name.RIGHT -> input[spaceship].right = isStarting
            Action.Name.TURBO -> input[spaceship].turbo = isStarting
            Action.Name.SHOOT -> if (action.type == Action.Type.START) input[spaceship].shoot = true
        }
    }
}
