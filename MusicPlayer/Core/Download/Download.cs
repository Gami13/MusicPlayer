namespace MusicPlayer;
using Plugin.Maui.Audio;
using System.Diagnostics;
using System.Net.Http.Headers;
using VideoLibrary;
using static MusicPlayer.Database;
public static partial class Download
{
	private static long chunkSize = 10_485_760;
	private static long _fileSize = 0L;



	private static async Task<long?> GetContentLengthAsync(string requestUri, bool ensureSuccess = true)
	{
		using (var request = new HttpRequestMessage(HttpMethod.Head, requestUri))
		{
			var response = await AppState.httpClient.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
			if (ensureSuccess)
				response.EnsureSuccessStatusCode();
			return response.Content.Headers.ContentLength;
		}
	}
	public static async Task SaveSong(YouTubeVideo video, Song song)
	{


		var (songPath, songLength) = await DownloadSongAsync(video, new Progress<Tuple<long, long>>((Tuple<long, long> v) =>
		 {
			 var percent = (int)(v.Item1 * 100 / v.Item2);
			 Debug.WriteLine(string.Format("Downloading.. ( % {0} )", percent));
		 }));
		if (songPath != null)
		{

			song.StoragePath = songPath.ToString();
			song.Duration = songLength;


			Database.AddSong(song);

		}
	}

}