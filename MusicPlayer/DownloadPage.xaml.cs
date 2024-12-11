




using System.Diagnostics;
using Android.Content;
using Android.Net;
using Android.Provider;
using AndroidX.DocumentFile.Provider;
using VideoLibrary;
using static MusicPlayer.Database;

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
            TitleField.Text = AppState.SelectedForDownload?.Title ?? "";
            ArtistField.Text = AppState.SelectedForDownload?.ChannelTitle ?? "";
            AlbumField.Text = "";
            GenreField.Text = "";
            YearField.Text = "";
            CoverImage.Source = AppState.SelectedForDownload?.ThumbnailUrl ?? "";
        });








    }
    private async void DownloadSong(object sender, EventArgs e)
    {
        var song = AppState.SelectedForDownload;
        var video = YouTube.Default.GetAllVideos("https://youtube.com/watch?v=" + song.VideoId)
            .First(v => v.AudioFormat == AudioFormat.Aac);

        var uri = Android.Net.Uri.Parse(AppState.MusicDirectory.Replace("%3A", ":"));
        Debug.WriteLine("Authority: " + uri.Authority);

        if (OperatingSystem.IsAndroid())
        {
            var musicDir = DocumentFile.FromTreeUri(Android.App.Application.Context, uri);
            if (musicDir == null || !musicDir.IsDirectory)
            {
                throw new InvalidOperationException("Invalid music directory URI.");
            }
            // Create a new document in the selected directory 
            var docFile = musicDir.CreateFile("audio/aac", song.Title + video.FileExtension);
            if (docFile == null)
            {
                throw new InvalidOperationException("Failed to create document.");
            }
            Debug.WriteLine("Here2");

            using (var output = Android.App.Application.Context.ContentResolver.OpenOutputStream(docFile.Uri))
            using (var client = new HttpClient())
            using (var input = await client.GetStreamAsync(video.Uri))
            {
                byte[] buffer = new byte[16 * 1024];
                int read;
                while ((read = await input.ReadAsync(buffer, 0, buffer.Length)) > 0)
                {
                    await output.WriteAsync(buffer, 0, read);
                    Debug.WriteLine("Reading");
                }
                Debug.WriteLine("Done");
            }
        }





    }




}