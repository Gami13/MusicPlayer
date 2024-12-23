namespace MusicPlayer;

using AndroidX.DocumentFile.Provider;
using Plugin.Maui.Audio;
using System.Diagnostics;
using System.Net.Http.Headers;
using VideoLibrary;
using static MusicPlayer.Database;
public static partial class Download
{





	public static async Task<(string, int)> DownloadSongAsync(YouTubeVideo video, IProgress<Tuple<long, long>> progress)
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


		var docFile = musicDir.CreateFile("audio/" + video.AudioFormat, video.Title);
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
				var to = ((i + 1) * chunkSize) - 1;
				var request = new HttpRequestMessage(HttpMethod.Get, uri);
				request.Headers.Range = new RangeHeaderValue(from, to);
				using (request)
				{
					var response = await AppState.httpClient.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
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
		return (docFile.Uri.ToString() ?? string.Empty, video.Info.LengthSeconds.GetValueOrDefault(0));
	}


}