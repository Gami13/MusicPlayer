
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
        public static string Name(this Code language)
        => LanguageDisplayNames[language];


        private static Dictionary<Code, string> LanguageDisplayNames
               = Enum.GetValues<Code>()
                .ToDictionary(
                    lang => lang,
                    lang => new CultureInfo(lang.ToCultureCode()).NativeName.ToTitleCase()
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




        }
    }

    public static class Utilities
    {








    }
}