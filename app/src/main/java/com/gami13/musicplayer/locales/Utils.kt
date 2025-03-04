package com.gami13.musicplayer.locales

import android.util.Log
import androidx.core.os.LocaleListCompat

import java.util.Locale



fun LocaleCode.toListCompat(): LocaleListCompat{
  return LocaleListCompat.forLanguageTags(this.code)
}

fun LocaleCode.formatName():String{

  val code = this.code
  val locale = Locale.forLanguageTag(code)

  val codeParts = code.split("-")
  var name = locale.getDisplayLanguage(locale).toTitleCase()

  if (codeParts.size > 1 && codeParts[0].lowercase() != codeParts[1].lowercase()) {
    name = "$name (${codeParts[1]})"
  }

  return name



}

fun LocaleCode.Companion.new(code: String): LocaleCode {
  return LocaleCode.valueOf(code.uppercase().replace("-","_"))

}


 fun String.toTitleCase(): String {
  return this.split(" ")
    .joinToString(" ") { word ->
      if (word.isNotEmpty()) word[0].uppercase() + word.substring(1).lowercase()
      else word
    }
}