




using System.Diagnostics;
using System.Net.Http.Headers;
using Android.Content;
using Android.Net;
using Android.Provider;
using AndroidX.DocumentFile.Provider;
using VideoLibrary;
using static MusicPlayer.Database;

namespace MusicPlayer;

public partial class DownloadPage : ContentView
{

    private HttpClient _client = new HttpClient();

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


        var youtube = YouTube.Default;
        var song = AppState.SelectedForDownload;
        var video = youtube.GetAllVideos("https://youtube.com/watch?v=" + song.VideoId)
            .First(v => v.AudioFormat == AudioFormat.Aac);

        if (OperatingSystem.IsAndroid())
        {
            var songPath = await CreateDownloadAsync(video, new Progress<Tuple<long, long>>((Tuple<long, long> v) =>
             {
                 var percent = (int)(v.Item1 * 100 / v.Item2);
                 Debug.WriteLine(string.Format("Downloading.. ( % {0} )", percent));
             }));
            if (songPath != null)
            {
                //TODO: ADD TO DATABASE
            }
        }
    }

    private long chunkSize = 10_485_760;
    private long _fileSize = 0L;


    public async Task<string> CreateDownloadAsync(YouTubeVideo video, IProgress<Tuple<long, long>> progress)
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