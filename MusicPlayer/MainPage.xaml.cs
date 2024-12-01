

namespace MusicPlayer;

public partial class MainPage : ContentView
{




    public MainPage()
    {
        InitializeComponent();




    }

    private void Button_Clicked(object sender, EventArgs e)
    {
        AppState.NavigationManager.NavigateTo(RouteKey.Download);
    }




}