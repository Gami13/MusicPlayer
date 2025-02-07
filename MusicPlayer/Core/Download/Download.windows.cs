namespace MusicPlayer;

using MimeTypes;
using Plugin.Maui.Audio;
using System.Diagnostics;
using System.Net.Http.Headers;
using VideoLibrary;
using static MusicPlayer.Database;
public static partial class Download {



	public static async Task<(string, int)> DownloadSongAsync(YouTubeVideo video, IProgress<(long, long)> progress) {
		var uri = new System.Uri(video.Uri);
		var path = AppState.MusicDirectory + "/" + video.Title + MimeTypeMap.GetExtension("audio/" + video.AudioFormat.ToString());
		var totalBytesCopied = 0L;

		Debug.WriteLine("Getting size");

		long fileSize;
		if (video.ContentLength == null) {
			fileSize = await GetContentLengthAsync(uri.AbsoluteUri) ?? 0;
		}
		else {
			fileSize = video.ContentLength.Value;
		}



		if (path == null) {
			throw new ArgumentNullException(nameof(path), "Music directory path cannot be null.");
		}


		using (var output = File.OpenWrite(path)) {

			var segmentCount = (int)Math.Ceiling(1.0 * fileSize / chunkSize);
			for (var i = 0; i < segmentCount; i++) {
				var from = i * chunkSize;
				var to = ((i + 1) * chunkSize) - 1;
				var request = new HttpRequestMessage(HttpMethod.Get, uri);
				request.Headers.Range = new RangeHeaderValue(from, to);
				using (request) {
					var response = await AppState.httpClient.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
					if (response.IsSuccessStatusCode)
						response.EnsureSuccessStatusCode();
					var stream = await response.Content.ReadAsStreamAsync();
					var buffer = new byte[81920];
					int bytesCopied;
					do {
						bytesCopied = await stream.ReadAsync(buffer, 0, buffer.Length);
						if (output != null)
							output.Write(buffer, 0, bytesCopied);

						totalBytesCopied += bytesCopied;
						progress.Report((totalBytesCopied, fileSize));
					} while (bytesCopied > 0);
				}
			}
		}
		return (path, video.Info.LengthSeconds);
	}


}