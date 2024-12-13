




using System.Diagnostics;
using System.Net.Http.Headers;
using Android.Content;
using Android.Net;
using Android.Provider;
using AndroidX.DocumentFile.Provider;
using Plugin.Maui.Audio;
using VideoLibrary;
using static MusicPlayer.Database;

namespace MusicPlayer;

public partial class DownloadPage : ContentView
{

    private HttpClient _client = new HttpClient();

    private byte[] _coverImageBytes;
    public DownloadPage()
    {
        InitializeComponent();

        AppState.SubscribeToNavigation(RouteKey.Download, () =>
        {

            _coverImageBytes = _client.GetByteArrayAsync(AppState.SelectedForDownload?.ThumbnailUrl).Result;


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

        if (OperatingSystem.IsAndroid())
        {
            var songPath = await DownloadSongAsync(video, new Progress<Tuple<long, long>>((Tuple<long, long> v) =>
             {
                 var percent = (int)(v.Item1 * 100 / v.Item2);
                 Debug.WriteLine(string.Format("Downloading.. ( % {0} )", percent));
             }));
            if (songPath != null)
            {
                var songStream = Android.App.Application.Context.ContentResolver.OpenInputStream(songPath);
                var songDuration = (int)AudioManager.Current.CreatePlayer(songStream).Duration;
                Debug.WriteLine("Song Duration: " + songDuration);
                //TODO: ADD TO DATABASE, crashes for some reason

                Debug.WriteLine("Title: " + TitleField.Text);
                Debug.WriteLine("Song Path: " + songPath);
                Debug.WriteLine("Duration: " + songDuration);
                Debug.WriteLine("Album: " + AlbumField.Text);
                Debug.WriteLine("Artist: " + ArtistField.Text);
                Debug.WriteLine("Genre: " + GenreField.Text);
                Debug.WriteLine("Year: " + YearField.Text);
                Debug.WriteLine("Cover bytes: " + Convert.ToBase64String(_coverImageBytes));
                Database.AddSong(new Song
                {
                    Title = TitleField.Text ?? song.Title,
                    StoragePath = songPath.ToString() ?? "",
                    Duration = songDuration,
                    // Album = AlbumField.Text ?? "",
                    // Artist = ArtistField.Text ?? "",
                    // Genre = GenreField.Text ?? "",
                    // Year = int.Parse(YearField.Text ?? "0"),
                    Cover = "test",
                    IsFavorite = false
                });


            }
        }
        var allSongs = Database.GetSongs();
        Debug.WriteLine("All Songs:");
        foreach (var s in allSongs)
        {
            Debug.WriteLine(s);
        }
    }

    private long chunkSize = 10_485_760;
    private long _fileSize = 0L;


    private async Task<Android.Net.Uri> DownloadSongAsync(YouTubeVideo video, IProgress<Tuple<long, long>> progress)
    {
        var uri = new System.Uri(video.Uri);
        var path = Android.Net.Uri.Parse(AppState.MusicDirectory.Replace("%3A", ":"));
        var totalBytesCopied = 0L;


        _fileSize = await GetContentLengthAsync(uri.AbsoluteUri) ?? 0;
        if (_fileSize == 0)
        {
            throw new Exception("File has no content !");
        }



        if (path == null)
        {
            throw new ArgumentNullException(nameof(path), "Music directory path cannot be null.");
        }



        var musicDir = DocumentFile.FromTreeUri(Android.App.Application.Context, path);
        if (musicDir == null || !musicDir.IsDirectory)
        {
            throw new InvalidOperationException("Invalid music directory URI.");
        }


        var docFile = musicDir.CreateFile("audio/aac", video.Title + video.FileExtension);
        if (docFile == null)
        {
            throw new InvalidOperationException("Failed to create document.");
        }



        if (Android.App.Application.Context.ContentResolver == null)
        {
            throw new InvalidOperationException("UwU");
        }
        using (var output = Android.App.Application.Context.ContentResolver.OpenOutputStream(docFile.Uri))
        {
            var segmentCount = (int)Math.Ceiling(1.0 * _fileSize / chunkSize);
            for (var i = 0; i < segmentCount; i++)
            {
                var from = i * chunkSize;
                var to = (i + 1) * chunkSize - 1;
                var request = new HttpRequestMessage(HttpMethod.Get, uri);
                request.Headers.Range = new RangeHeaderValue(from, to);
                using (request)
                {
                    var response = await _client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
                    if (response.IsSuccessStatusCode)
                        response.EnsureSuccessStatusCode();
                    var stream = await response.Content.ReadAsStreamAsync();
                    var buffer = new byte[81920];
                    int bytesCopied;
                    do
                    {
                        bytesCopied = await stream.ReadAsync(buffer, 0, buffer.Length);
                        if (output != null)
                            output.Write(buffer, 0, bytesCopied);

                        totalBytesCopied += bytesCopied;
                        progress.Report(new Tuple<long, long>(totalBytesCopied, _fileSize));
                    } while (bytesCopied > 0);
                }
            }
        }
        return docFile.Uri;
    }
    private async Task<long?> GetContentLengthAsync(string requestUri, bool ensureSuccess = true)
    {
        using (var request = new HttpRequestMessage(HttpMethod.Head, requestUri))
        {
            var response = await _client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
            if (ensureSuccess)
                response.EnsureSuccessStatusCode();
            return response.Content.Headers.ContentLength;
        }
    }

}