




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


    private byte[] _coverImageBytes;
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


        var youtube = YouTube.Default;
        var song = AppState.SelectedForDownload;
        if (song == null)

        {
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
            Cover = Convert.ToBase64String(_coverImageBytes),
            IsFavorite = false
        });
        var allSongs = Database.GetSongs();
        Debug.WriteLine("All Songs:");
        foreach (var s in allSongs)
        {
            Debug.WriteLine(s.Title + " " + s.Artist + " " + s.Album + " " + s.Genre + " " + s.Year + " " + s.Duration);
        }
    }


}