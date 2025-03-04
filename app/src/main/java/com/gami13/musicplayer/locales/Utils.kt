package com.gami13.musicplayer.utils

import androidx.core.os.LocaleListCompat
import com.gami13.musicplayer.LocaleCode
import java.util.Locale

fun localeToDisplayName(localeCode: LocaleCode): String {
  return Locale.forLanguageTag(localeCode.code).displayName
}

fun localeToLocale(localeCode: LocaleCode): Locale {
  return Locale.forLanguageTag(localeCode.code)
}

fun localeToLocaleListCompat(localeCode: LocaleCode): LocaleListCompat{
  return LocaleListCompat.forLanguageTags(localeCode.code)
}

fun stringToLocaleCode(code: String): LocaleCode{
  return LocaleCode.valueOf(code.uppercase().replace("-","_"))
}