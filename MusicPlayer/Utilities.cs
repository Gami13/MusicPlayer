
using System.Globalization;

namespace MusicPlayer
{
    public enum SupportedLanguage
    {
        PL_PL,
        EN_US


    }
    public static class Utilities
    {
        public static void ChangeLanguage(SupportedLanguage language)
        {
            CultureInfo culture;
            switch (language)
            {
                case SupportedLanguage.PL_PL:
                    culture = new CultureInfo("pl-PL");
                    break;
                case SupportedLanguage.EN_US:
                    culture = new CultureInfo("en-US");
                    break;
                default:
                    culture = new CultureInfo("en-US");
                    break;
            }
            CultureInfo.CurrentCulture = culture;
            CultureInfo.CurrentUICulture = culture;
            LocalizationResourceManager.Instance.SetCulture(culture);




        }

    }
}