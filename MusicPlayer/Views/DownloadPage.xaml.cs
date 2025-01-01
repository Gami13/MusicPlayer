




using CommunityToolkit.Maui.Alerts;
using Plugin.Maui.Audio;
using System.Buffers.Text;
using System.Diagnostics;
using System.Net.Http.Headers;
using VideoLibrary;
using static MusicPlayer.Database;

namespace MusicPlayer;
#if ANDROID
using Android;
#endif

public partial class DownloadPage : ContentView
{


    private bool _isDownloading = false;

    private byte[]? _coverImageBytes;
    public DownloadPage()
    {
        InitializeComponent();

        AppState.SubscribeToNavigation(RouteKey.Download, () =>
        {

            _coverImageBytes = AppState.httpClient.GetByteArrayAsync(AppState.SelectedForDownload?.ThumbnailUrl).Result;


            Debug.WriteLine("DownloadPage: OnNavigate");
            Debug.WriteLine($"SelectedForDownload: {AppState.SelectedForDownload?.Title}");
            TitleField.Text = AppState.SelectedForDownload?.Title ?? "";
            ArtistField.Text = AppState.SelectedForDownload?.ChannelTitle ?? "";
            AlbumField.Text = "";
            GenreField.Text = "";
            YearField.Text = "";
            CoverImage.Source = ImageSource.FromStream(() => new MemoryStream(_coverImageBytes));
        });

    }
    private async void DownloadSong(object sender, EventArgs e)
    {

        if (string.IsNullOrEmpty(AppState.MusicDirectory))
        {
            await Toast.Make("Music Directory not set").Show();
            return;
        }

        if (_isDownloading)
        {
            return;
        }
        _isDownloading = true;

        var youtube = YouTube.Default;
        var song = AppState.SelectedForDownload;
        if (song == null)

        {
            _isDownloading = false;
            return;

        }
        var video = youtube.GetAllVideos("https://youtube.com/watch?v=" + song.VideoId)
            .First(v => v.AudioFormat == AudioFormat.Aac);


        Debug.WriteLine("Saving song");



        int songYear = YearField.Text != "" ? int.Parse(YearField.Text) : 0;
        await Download.SaveSong(video, new Song
        {

            Title = TitleField.Text ?? song.Title,
            Album = AlbumField.Text ?? "",
            Artist = ArtistField.Text ?? "",
            Genre = GenreField.Text ?? "",
            Year = songYear,
            Cover = _coverImageBytes != null ? Convert.ToBase64String(_coverImageBytes) : string.Empty,
            IsFavorite = false
        }, new Progress<Tuple<long, long>>((Tuple<long, long> v) =>
         {
             var percent = v.Item1 * 100 / v.Item2;
             ProgressIndicator.Percent = percent;
             ProgressLabel.Text = (int)percent + "%";
             Debug.WriteLine(string.Format("Downloading.. ( % {0} )", percent));
         }));
        var allSongs = Database.GetSongs();
        Debug.WriteLine("All Songs:");
        foreach (var s in allSongs)
        {
            Debug.WriteLine(s.Title + " " + s.Artist + " " + s.Album + " " + s.Genre + " " + s.Year + " " + s.Duration);
        }
        _isDownloading = false;
        AppState.NavigationManager.NavigateTo(RouteKey.Home);
    }


}