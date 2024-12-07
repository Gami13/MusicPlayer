




using System.Diagnostics;

namespace MusicPlayer;

public partial class DownloadPage : ContentView
{



    public DownloadPage()
    {
        InitializeComponent();

        AppState.SubscribeToNavigation(RouteKey.Download, () =>
        {
            Debug.WriteLine("DownloadPage: OnNavigate");
            Debug.WriteLine($"SelectedForDownload: {AppState.SelectedForDownload?.Title}");
            SongTitle.Text = AppState.SelectedForDownload?.Title ?? "";
        });








    }






}