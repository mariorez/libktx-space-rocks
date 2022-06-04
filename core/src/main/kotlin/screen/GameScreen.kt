package screen

import Action
import BaseScreen
import GameBoot.Companion.WINDOW_HEIGHT
import GameBoot.Companion.WINDOW_WIDTH
import WorldSize
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import component.InputComponent
import component.PlayerComponent
import component.RenderComponent
import component.TransformComponent
import component.WrapAroundWorldComponent
import ktx.ashley.entity
import ktx.ashley.with
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import system.InputSystem
import system.PlayerMovementSystem
import system.RenderingSystem
import system.WrapAroundWorldSystem
import kotlin.random.Random.Default.nextInt

class GameScreen(
    private val assets: AssetStorage
) : BaseScreen() {
    private val engine = PooledEngine()
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera(
        WINDOW_WIDTH.toFloat(),
        WINDOW_HEIGHT.toFloat()
    ).apply { setToOrtho(false) }
    private val worldSize = WorldSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    private lateinit var spaceship: Entity
    private var rocksQuantity = 10

    init {
        registerAction(Input.Keys.W, Action.Name.UP)
        registerAction(Input.Keys.A, Action.Name.LEFT)
        registerAction(Input.Keys.D, Action.Name.RIGHT)

        spawnPlayer()
        spawnRocks()

        engine.apply {
            entity {
                with<TransformComponent> { zIndex = (rocksQuantity + 1).toFloat() }
                with<RenderComponent> { sprite = Sprite(assets.get<Texture>("space.png")) }
            }

            addSystem(InputSystem(spaceship))
            addSystem(PlayerMovementSystem(spaceship))
            addSystem(WrapAroundWorldSystem(worldSize))
            addSystem(RenderingSystem(batch, camera))
        }
    }

    private fun spawnPlayer() {
        spaceship = engine.entity {
            with<PlayerComponent>()
            with<WrapAroundWorldComponent>()
            with<InputComponent>()
            with<TransformComponent> {
                position.set((WINDOW_WIDTH / 2).toFloat(), (WINDOW_HEIGHT / 2).toFloat())
                acceleration = 200f
                deceleration = 10f
                maxSpeed = 100f
                degreesPerSecond = 120f
            }
            with<RenderComponent> {
                sprite = Sprite(assets.get<Texture>("spaceship.png"))
            }
        }
    }

    private fun spawnRocks() {
        val rockImage = assets.get<Texture>("rock.png")
        (1..rocksQuantity).forEach {
            engine.entity {
                with<TransformComponent> {
                    position.x = nextInt(0, WINDOW_WIDTH - rockImage.width).toFloat()
                    position.y = nextInt(0, WINDOW_HEIGHT - rockImage.height).toFloat()
                    zIndex = it.toFloat()
                }
                with<RenderComponent> { sprite = Sprite(rockImage) }
            }
        }
    }

    override fun doAction(action: Action) {
        val input = InputComponent.mapper.get(spaceship)
        val isStarting = action.type == Action.Type.START
        when (action.name) {
            Action.Name.UP -> input.up = isStarting
            Action.Name.LEFT -> input.left = isStarting
            Action.Name.RIGHT -> input.right = isStarting
        }
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun dispose() {
        batch.disposeSafely()
        assets.disposeSafely()
        uiStage.disposeSafely()
    }
}