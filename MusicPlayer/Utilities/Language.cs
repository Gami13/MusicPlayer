using System.Globalization;

namespace MusicPlayer
{
	public static class Language
	{
		private static string ToTitleCase(this string input)
		{
			return string.Join(" ", input.Split('_')
				.Select(word => CultureInfo.CurrentCulture.TextInfo.ToTitleCase(word.ToLower())));
		}
		public enum Code
		{
			PL_PL,
			EN_US
		}
		public static Code[] Codes = Enum.GetValues<Code>();
		private static string ToCultureCode(this Code language)
		{
			var parts = language.ToString().Split("_");
			return parts[0].ToLower() + "-" + parts[1];
		}

		private static string getFormattedLanguageName(Code lang)

		{
			var code = lang.ToCultureCode();
			var culture = new CultureInfo(code);
			var codeParts = code.Split("-");
			//Language name with brackets removed
			string name = culture.NativeName.Split("(")[0].Trim().ToTitleCase();
			if (codeParts[0].ToLower() != codeParts[1].ToLower())
			{
				name = name + " (" + codeParts[1] + ")";
			}
			return name;
		}
		public static string Name(this Code language)
		=> LanguageDisplayNames[language];
		private static Dictionary<Code, string> LanguageDisplayNames
			   = Enum.GetValues<Code>()
				.ToDictionary(
					lang => lang,
					lang => getFormattedLanguageName(lang)
				);
		private static Dictionary<string, Code> SupportedLanguages = Enum.GetValues<Code>().ToDictionary(
			lang => lang.ToCultureCode(),
			lang => lang
		);

		public static Code GetCurrent()
		{
			return SupportedLanguages[CultureInfo.CurrentCulture.Name];
		}
		public static void Change(Code language)
		{
			CultureInfo culture = new CultureInfo(language.ToCultureCode());
			CultureInfo.CurrentCulture = culture;
			CultureInfo.CurrentUICulture = culture;
			LocalizationResourceManager.Instance.SetCulture(culture);
			AppState.PreferredLanguage = language;
		}
		public static void SetLocalizedBinding(VisualElement element, BindableProperty property, TranslationKey key, params object[] parameters)
		{
			var extension = new TranslateExtension
			{
				Key = key,
				Parameters = new System.Collections.ObjectModel.Collection<object>(parameters)
			};
			element.SetBinding(property, extension.ProvideValue(serviceProvider: null!));
		}

		public static string Localize(TranslationKey key, params object[] parameters)
		{

			return string.Format(LocalizationResourceManager.Instance[key.ToString()], parameters);
		}

	}
}