using System.Diagnostics;
using System.Globalization;
using System.Text.Json;

namespace MusicPlayer {

	public struct RelativeTime {
		public int Value { get; set; }
		public TranslationKey Unit { get; set; }
	}

	public static partial class Utilities {


		public static async Task<SearchResultItem[]> SearchYoutube(string searchTerm) {
			string url = Constants.YoutubeSearchUrl(searchTerm);
			Debug.WriteLine(url);
			using (HttpClient client = new HttpClient()) {

				using (HttpResponseMessage response = await client.GetAsync(url)) {

					if (response.IsSuccessStatusCode) {
						string json = await response.Content.ReadAsStringAsync();
						YouTubeSearchResponse? searchResponse = JsonSerializer.Deserialize<YouTubeSearchResponse>(json);

						if (searchResponse?.Items != null && searchResponse.Items.Count > 0) {
							return searchResponse.Items.ToArray();
						}
						return Array.Empty<SearchResultItem>();
					}
				}
			}

			return [];

		}
		public static RelativeTime ISOToRelativeTime(string isoTime) {
			DateTime utcTime = DateTime.Parse(isoTime, CultureInfo.InvariantCulture, DateTimeStyles.AdjustToUniversal);
			TimeSpan diff = DateTime.UtcNow - utcTime;
			double days = diff.TotalDays;
			double hours = diff.TotalHours;

			RelativeTime PluralTime(double baseAmount, double unit, TranslationKey single, TranslationKey plural) {
				int v = (int)(baseAmount / unit);
				return new RelativeTime { Value = v, Unit = v == 1 ? single : plural };
			}

			if (days >= 365) return PluralTime(days, 365, TranslationKey.yearAgo, TranslationKey.yearsAgo);
			if (days >= 30) return PluralTime(days, 30, TranslationKey.monthAgo, TranslationKey.monthsAgo);
			if (days >= 7) return PluralTime(days, 7, TranslationKey.weekAgo, TranslationKey.weeksAgo);
			if (days >= 1) return PluralTime(days, 1, TranslationKey.dayAgo, TranslationKey.daysAgo);
			if (hours >= 1) return PluralTime(hours, 1, TranslationKey.hourAgo, TranslationKey.hoursAgo);

			return new RelativeTime { Value = 1, Unit = TranslationKey.hourAgo };
		}

		public static string GetDurationString(int duration) {
			int minutes = duration / 60;
			int seconds = duration % 60;
			return $"{minutes}:{seconds:00}";
		}





	}
}