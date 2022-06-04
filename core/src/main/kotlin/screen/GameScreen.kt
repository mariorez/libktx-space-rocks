package screen

import Action
import BaseScreen
import GameBoot.Companion.WINDOW_HEIGHT
import GameBoot.Companion.WINDOW_WIDTH
import WorldSize
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import component.InputComponent
import component.PlayerComponent
import component.RenderComponent
import component.RockComponent
import component.TransformComponent
import component.WrapAroundWorldComponent
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import system.InputSystem
import system.MovementSystem
import system.RenderingSystem
import system.WrapAroundWorldSystem
import kotlin.properties.Delegates
import kotlin.random.Random.Default.nextInt

class GameScreen(
    private val assets: AssetStorage
) : BaseScreen() {
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera(
        WINDOW_WIDTH.toFloat(), WINDOW_HEIGHT.toFloat()
    ).apply { setToOrtho(false) }
    private val worldSize = WorldSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    private var spaceship: Entity by Delegates.notNull()
    private var rocksQuantity = 10
    private val world = World {
        inject(batch)
        inject(camera)
        inject(worldSize)
        system<InputSystem>()
        system<MovementSystem>()
        system<WrapAroundWorldSystem>()
        system<RenderingSystem>()
    }

    init {
        registerAction(Input.Keys.W, Action.Name.UP)
        registerAction(Input.Keys.A, Action.Name.LEFT)
        registerAction(Input.Keys.D, Action.Name.RIGHT)

        spawnPlayer()
        spawnRocks()

        world.apply {
            entity {
                add<TransformComponent>()
                add<RenderComponent> { sprite = Sprite(assets.get<Texture>("space.png")) }
            }

            systems.forEach {
                when (it::class) {
                    InputSystem::class -> (it as InputSystem).player = spaceship
                }
            }
        }
    }

    private fun spawnPlayer() {
        spaceship = world.entity {
            add<PlayerComponent>()
            add<WrapAroundWorldComponent>()
            add<InputComponent>()
            add<TransformComponent> {
                position.set((WINDOW_WIDTH / 2).toFloat(), (WINDOW_HEIGHT / 2).toFloat())
                acceleration = 200f
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
        repeat(rocksQuantity) {
            world.entity {
                add<RockComponent>()
                add<WrapAroundWorldComponent>()
                add<RenderComponent> { sprite = Sprite(rockImage) }
                add<TransformComponent> {
                    position.x = nextInt(0, WINDOW_WIDTH - rockImage.width).toFloat()
                    position.y = nextInt(0, WINDOW_HEIGHT - rockImage.height).toFloat()
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
            Action.Name.LEFT -> input.left = isStarting
            Action.Name.RIGHT -> input.right = isStarting
        }
    }

    override fun render(delta: Float) {
        world.update(delta)
    }

    override fun dispose() {
        world.dispose()
        batch.disposeSafely()
        assets.disposeSafely()
        uiStage.disposeSafely()
    }
}