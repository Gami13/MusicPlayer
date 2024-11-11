using System.Diagnostics;
using System.Globalization;

namespace MusicPlayer;

public partial class MainPage : ContentPage
{




    public MainPage()
    {
        InitializeComponent();
        LocalizationResourceManager.Instance.PropertyChanged += (_, _) => { };




    }




}