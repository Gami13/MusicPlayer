import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GenerateLocaleCodeEnumTask : DefaultTask() {
  @TaskAction
  fun action() {
    println("Hello " + project.name)

    val outputDir = project.file("./src/main/java/com/gami13/musicplayer")
    val targetDir = project.file("./src/main/res")
    val languages = targetDir.listFiles { file -> file.isDirectory }?.map { it.name }?.filter {
      it.contains("values-")
    }?.map { it.replace("values-", "").replace("-r", "-").replace("-", "_") }
      ?: emptyList<String>()
    println("langs: $languages")
    val languageStrings = languages.map {
      it.uppercase() + "(\"" + it + "\"),\n"
    }.joinToString("")
    outputDir.mkdirs()
    project.file("$outputDir/LanguageList.kt").writeText(
      "package com.gami13.musicplayer\nenum " +
              "class LocaleCode(val code: String){\n$languageStrings}"
    )
  }
}


