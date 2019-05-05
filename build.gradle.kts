import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openjfx.gradle.JavaFXOptions

plugins {
    kotlin("jvm") version "1.3.11"
    id("org.jetbrains.dokka") version "0.9.17"
    id("maven-publish")
    id("signing")
    id("org.openjfx.javafxplugin") version "0.0.7"
    id("maven")
}

group = "kfoenix"
version = "0.1.4"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}


dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("com.jfoenix:jfoenix:9.0.8")
    compile("no.tornado:tornadofx:2.0.0-SNAPSHOT") {
        exclude("org.jetbrains.kotlin")
    }
}


with(tasks) {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    withType<DokkaTask> {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].java.srcDirs)
}

val javaDocJar by tasks.creating(Jar::class) {
    classifier = "javadoc"
    from("$buildDir/javadoc")
}
configure<JavaFXOptions>
{
   modules("javafx.controls", "javafx.fxml")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = "com.github.bkenn"
            artifactId = "kfoenix"
            version = "${project.version}"

            signing {
                sign(this@register)
            }

            from(components["java"])
            artifact(sourcesJar)
            artifact(javaDocJar)

            pom {
                name.set("KFoenix")
                description.set("A TornadoFX dsl for JFoenix")
                url.set("https://github.com/bkenn/KFoenix")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("bkenn")
                        name.set("Brian Kennedy")
                        email.set("briankennedy392@gmail.com")
                    }
                }

                scm {
                    url.set("https://github.com/bkenn/KFoenix")
                    connection.set("scm:git:git://github.com/bkenn/KFoenix.git")
                    developerConnection.set("scm:git:ssh://github.com/bkenn/KFoenix.git")
                }

                distributionManagement {
                    repositories {
                        maven {
                            credentials {
                                // place credentials in ~/.gradle/gradle.properties
                                username = ext.properties["mavenUser"] as String?
                                password = ext.properties["mavenPassword"] as String?
                            }

                            val snapshotUrl = "https://oss.sonatype.org/content/repositories/snapshots"
                            val releaseUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"

                            if (version.endsWith("-SNAPSHOT"))
                            {
                                setUrl(snapshotUrl)
                            } else
                            {
                                setUrl(releaseUrl)
                            }
                        }
                    }
                }
            }
        }
    }
}
