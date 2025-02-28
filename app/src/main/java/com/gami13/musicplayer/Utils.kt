package com.gami13.musicplayer

import java.util.Locale

fun LocaleToDisplayName(localeCode: LocaleCode):String{
    return Locale.forLanguageTag(localeCode.code).displayName
}