package io.github.fourlastor.entity

import java.io.File

fun demoData(): Entities {
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
}
