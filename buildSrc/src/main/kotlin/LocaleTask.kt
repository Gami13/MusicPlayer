import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
abstract class GenerateLocaleCodeEnumTask : DefaultTask() {
  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val resourcesDir: DirectoryProperty

  @get:OutputFile
  abstract val outputFile: RegularFileProperty

  @TaskAction
  fun action() {
    val outputDir = outputFile.get().asFile.parentFile
    val targetDir = resourcesDir.get().asFile
    val languages = targetDir.listFiles { file -> file.isDirectory }?.asSequence()?.map { it.name }
      ?.filter {
        it.contains("values-")
      }?.toMutableList()
    languages?.add("values-en-rUS")

    val finals = (languages?.map {
      it.replace("values-", "").replace("-r", "-")
      //        .replace("-", "_")
    }?.joinToString("") {
      it.uppercase().replace("-", "_") + "(\"" + it + "\"),\n"
    }?.dropLast(2) + ";") ?: ""
    outputDir.mkdirs()


    outputFile.get().asFile.writeText(
      "package com.gami13.musicplayer.locales\nenum class LocaleCode(val code: String)" +
              "{\n$finals \ncompanion object { }}"
    )
  }
}