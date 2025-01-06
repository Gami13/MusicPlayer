using CommunityToolkit.Maui.Alerts;
using System.Diagnostics;
using VideoLibrary;
namespace MusicPlayer;

public partial class DownloadPage : ContentView {
	private bool _isDownloading = false;
	private byte[]? _coverImageBytes;
	public DownloadPage() {
		InitializeComponent();
		AppState.SubscribeToNavigation(RouteKey.Download, () => {
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
	private async void DownloadSong(object sender, EventArgs e) {

		if (string.IsNullOrEmpty(AppState.MusicDirectory)) {
			await Toast.Make("Music Directory not set").Show();
			return;
		}
		if (_isDownloading) {
			return;
		}
		_isDownloading = true;
		var song = AppState.SelectedForDownload;
		if (song == null) {
			return;
		}
		if (!int.TryParse(YearField.Text, out int songYear)) {
			_isDownloading = false;
			await Toast.Make("Invalid year").Show();
			return;
		}
		var newSong = new Database.Song {
			Title = TitleField.Text ?? song.Title,
			Album = AlbumField.Text ?? "",
			Artist = ArtistField.Text ?? "",
			Genre = GenreField.Text ?? "",
			Year = songYear,
			Cover = _coverImageBytes != null ? Convert.ToBase64String(_coverImageBytes) : "",
			IsFavorite = false
		};
		await Task.Run(() => downloadSong(newSong, song.VideoId));
	}
	private async Task downloadSong(Database.Song song, string videoId) {
		try {

			var youtube = YouTube.Default;
			var videoUrl = $"https://youtube.com/watch?v={videoId}";
			var video = youtube.GetAllVideos(videoUrl)
				.First(v => v.AudioFormat == AudioFormat.Aac);
			var progress = new Progress<(long, long)>(v => {
				var percent = v.Item1 * 100 / v.Item2;
				MainThread.BeginInvokeOnMainThread(() => {
					ProgressIndicator.Percent = percent;
					ProgressLabel.Text = $"{percent}%";
				});
				Debug.WriteLine($"Downloading.. (% {percent})");
			});
			await Download.SaveSong(video, song, progress);
			var allSongs = Database.GetSongs();
			Debug.WriteLine("All Songs:");
			foreach (var s in allSongs) {
				Debug.WriteLine($"{s.Title} {s.Artist} {s.Album} {s.Genre} {s.Year} {s.Duration}");
			}
			await MainThread.InvokeOnMainThreadAsync(() => {
				_isDownloading = false;
				AppState.NavigationManager?.NavigateTo(RouteKey.Home);
			});
		}
		catch (Exception e) {
			Debug.WriteLine(e.Message);
			await MainThread.InvokeOnMainThreadAsync(() => {
				_isDownloading = false;
				Toast.Make("Failed to download song").Show();
			});
		}
	}
}