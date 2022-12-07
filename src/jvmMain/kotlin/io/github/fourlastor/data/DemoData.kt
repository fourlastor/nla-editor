package io.github.fourlastor.data

import java.io.File

fun demoData(): VersionedProject.V1 {
    val imgPath = File("src/jvmMain/resources/player.png").absolutePath
    return Entities.empty()
        .image(0, "Hero", imgPath)
        .group(0, "Group")
        .image(
            2, "Mini hero", imgPath, Transform.IDENTITY.copy(
                rotation = 90f,
                scale = 0.4f,
            )
        )
        .let { VersionedProject.V1(it) }
}