package io.github.fourlastor.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import okio.FileSystem
import okio.Path
import kotlin.time.Duration

class ViewModel(
    private val scope: CoroutineScope,
    private val fileSystem: FileSystem,
    private val path: Path,
) {
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
        ) { entities, animations ->
            LoadableProject.Loaded(entities, animations, path)
        }

    @OptIn(ExperimentalSerializationApi::class)
    fun load() {
        scope.launch {
            withContext(Dispatchers.IO) {
                if (fileSystem.exists(path)) {
                    fileSystem.read(path) {
                        inputStream().use {
                            Json.decodeFromStream(
                                stream = it,
                                deserializer = PersistableProject.serializer()
                            ).migrateToLatest()
                        }.also {
                            updateWith(it)
                        }
                    }
                } else {
                    save()
                }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun save() {
        fileSystem.write(path) {
            val project = LatestProject(
                entities.value,
                animations.value,
                entityIds.value,
                propertyIds.value,
                animationIds.value,
            )
            outputStream().use {
                Json.encodeToStream(
                    value = project,
                    stream = it,
                    serializer = PersistableProject.serializer(),
                )
            }
        }
    }

    /** This will be useful in the future, to version the projects. */
    private fun PersistableProject.migrateToLatest(): LatestProject = when (this) {
        is PersistableProject.V1 -> this
    }

    private fun updateWith(project: LatestProject) {
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

    private fun property(value: Float) = PropertyValue(
        id = propertyIds.nextId(),
        value = value,
    )

    private fun MutableStateFlow<Long>.nextId() = updateAndGet { it + 1 }
    fun updateAnimation(id: Long, update: (Animation) -> Animation) {
        animations.update { it.animation(update(it[id])) }
    }
}
