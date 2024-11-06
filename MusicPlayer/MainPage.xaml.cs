using System.Diagnostics;
using System.Globalization;

namespace MusicPlayer;

public partial class MainPage : ContentPage
{




    public MainPage()
    {
        InitializeComponent();




    }


    private void ToggleLanguage(object sender, EventArgs e)
    {
        if (CultureInfo.CurrentCulture.Name == "en-US")
        {
            CultureInfo.CurrentCulture = new CultureInfo("pl-PL");
            CultureInfo.CurrentUICulture = new CultureInfo("pl-PL");
            Debug.WriteLine("Switched to Polish");
        }
        else
        {
            CultureInfo.CurrentCulture = new CultureInfo("en-US");
            CultureInfo.CurrentUICulture = new CultureInfo("en-US");
        }
#pragma warning disable CS8602 // Dereference of a possibly null reference.
        (Application.Current as App).MainPage = new AppShell();
#pragma warning restore CS8602 // Dereference of a possibly null reference.

    }



}