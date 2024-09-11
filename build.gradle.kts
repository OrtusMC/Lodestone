plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "1.0.17"
}

tasks.named<Wrapper>("wrapper") {
    // Define wrapper values here so as to not have to always do so when updating gradlew.properties.
    // Switching this to Wrapper.DistributionType.ALL will download the full gradle sources that comes with
    // documentation attached on cursor hover of gradle classes and methods. However, this comes with increased
    // file size for Gradle. If you do switch this to ALL, run the Gradle wrapper task twice afterwards.
    // (Verify by checking gradle/wrapper/gradle-wrapper.properties to see if distributionUrl now points to `-all`)
    distributionType = Wrapper.DistributionType.BIN
}
group = "${property("mod_group_id")}"

repositories {
    mavenLocal()
}

base {
    archivesName.set("${property("mod_id")}")
}

// Mojang ships Java 21 to end users starting in 1.20.5, so mods should target Java 21.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

neoForge {
    version.set(project.property("neo_version").toString())

    parchment {
        mappingsVersion.set(project.property("parchment_mappings_version").toString())
        minecraftVersion.set(project.property("parchment_minecraft_version").toString())
    }

    // This line is optional. Access Transformers are automatically detected
    // accessTransformers.set(project.files("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        register("client") {
            client()

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id").toString())
        }

        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id").toString())
        }

        register("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id").toString())
        }

        register("data") {
            data()
            programArguments.addAll(
                "--mod", project.property("mod_id").toString(),
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create("${property("mod_id")}") {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Include resources generated by data generators.
sourceSets {
    main {
        resources.srcDir("src/generated/resources")
    }
}

repositories {
    mavenCentral()
    maven {
        name = "OctoStudios"
        url = uri("https://maven.octo-studios.com/releases")
    }
    maven {
        name = "JEI maven"
        url = uri("https://dvs1.progwml6.com/files/maven")
    }
    maven {
        name = "tterrag maven"
        url = uri("https://maven.tterrag.com/")
    }
    maven {
        name = "BlameJared maven"
        url = uri("https://maven.blamejared.com/")
    }
    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {

    //runtimeOnly("mezz.jei:jei-${property("minecraft_version")}-neoforge:${property("jei_version")}")

    // Curios dependency
    compileOnly("top.theillusivec4.curios:curios-neoforge:${property("curios_version")}:api")
    runtimeOnly("top.theillusivec4.curios:curios-neoforge:${property("curios_version")}")

//    implementation(fg.deobf("com.sammy.malum:malum:${minecraftVersion}-1.6.72"))
}
val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "minecraft_version" to project.findProperty("minecraft_version") as String,
        "minecraft_version_range" to project.findProperty("minecraft_version_range") as String,
        "neo_version" to project.findProperty("neo_version") as String,
        "neo_version_range" to project.findProperty("neo_version_range") as String,
        "loader_version_range" to project.findProperty("loader_version_range") as String,
        "mod_id" to project.findProperty("mod_id") as String,
        "mod_name" to project.findProperty("mod_name") as String,
        "mod_license" to project.findProperty("mod_license") as String,
        "mod_version" to project.findProperty("mod_version") as String,
        "mod_authors" to project.findProperty("mod_authors") as String,
        "mod_description" to project.findProperty("mod_description") as String
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)

    // Exclude .java files or any other files that shouldn't have template expansion
    filesMatching("**/*.java") {
        exclude()
    }

    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}
// Include the output of "generateModMetadata" as an input directory for the build.
// This works with both building through Gradle and the IDE.
sourceSets["main"].resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)


java {
    withJavadocJar()
    withSourcesJar()
}
/*
tasks.withType<Jar> {
    val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
    manifest {
        attributes(mapOf(
                "Specification-Title" to "${property("mod_name")}",
                "Specification-Vendor" to "${property("mod_authors")}",
                "Specification-Version" to '1',
                "Implementation-Title" to "${property("mod_name")}",
                "Implementation-Version" to "${property("version")}",
                "Implementation-Vendor" to "${property("mod_authors")}",
                "Implementation-Timestamp" to now,
        ))
    }
    finalizedBy("reobfJar")
}

 */

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = "${property("mod_id")}"
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file://${System.getenv("local_maven")}")
        }
    }
}

idea {
    module {
        for (fileName in listOf("run", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}