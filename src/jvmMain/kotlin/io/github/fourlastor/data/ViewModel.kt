package io.github.fourlastor.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlin.time.Duration

class ViewModel {
    private val entityIds = MutableStateFlow(0L)
    private val propertyIds = MutableStateFlow(0L)
    private val animationIds = MutableStateFlow(0L)

    private val entities = MutableStateFlow(Entities.empty().entity(createRootGroup()))
    private val animations = MutableStateFlow(Animations.empty())

    private fun createRootGroup() = Group(
        id = 0,
        name = "Root",
        parentId = null,
        transform = transform(),
        collapsed = false
    )

    val project: Flow<LoadableProject>
        get() = combine(
            entities,
            animations,
            entityIds,
            animationIds,
            propertyIds
        ) { entities, animations, entityId, animationId, propertyId ->
            LoadableProject.Loaded(
                PersistableProject.V1(entities, animations, entityId, animationId, propertyId)
            )
        }

    fun load(project: LatestProject) {
        entities.update { project.entities }
        animations.update { project.animations }
        entityIds.update { project.lastEntityId }
        propertyIds.update { project.lastPropertyId }
        animationIds.update { project.lastAnimationId }
    }

    fun animation(name: String, duration: Duration) {
        animations.update {
            it.animation(
                Animation(
                    id = animationIds.nextId(),
                    name,
                    duration,
                    emptyMap()
                )
            )
        }
    }

    @Suppress("unused") // To be used soon
    fun keyFrame(
        animationId: Long,
        entityId: Long,
        propertyId: Long,
        position: Duration,
        value: Float,
    ) {
        animations.update {
            it.keyFrame(
                animationId,
                entityId,
                propertyId,
                position,
                value
            )
        }
    }

    fun deleteEntity(id: Long) {
        entities.update { it.remove(id) }
    }

    fun image(
        parentId: Long,
        name: String,
        path: String,
    ) {
        entities.update {
            it.entity(
                Image(
                    id = entityIds.nextId(),
                    parentId = parentId,
                    name = name,
                    transform = transform(),
                    path = path,
                    collapsed = false,
                    frame = frame(),
                )
            )
        }
    }

    fun group(
        parentId: Long,
        name: String,
    ) {
        entities.update {
            it.entity(
                Group(
                    id = entityIds.nextId(),
                    parentId = parentId,
                    name = name,
                    transform = transform(),
                    collapsed = false,
                )
            )
        }
    }

    fun updateEntity(entity: Long, update: (entity: Entity) -> Entity) {
        entities.update { it.entity(update(it[entity])) }
    }

    private fun transform() = Transform(
        xProperty = property(0f),
        yProperty = property(0f),
        rotationProperty = property(0f),
        scaleProperty = property(1f),
    )

    private fun frame() = Frame(
        rowsProperty = property(2f),
        columnsProperty = property(2f),
        frameNumberProperty = property(2f),
    )

    private fun property(value: Float) = PropertyValue(
        id = propertyIds.nextId(),
        value = value,
    )

    private fun MutableStateFlow<Long>.nextId() = updateAndGet { it + 1 }
    fun updateAnimation(id: Long, update: (Animation) -> Animation) {
        animations.update { it.animation(update(it[id])) }
    }
}
