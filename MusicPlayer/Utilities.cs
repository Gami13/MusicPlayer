using System.Diagnostics;
using System.Text.Json;

namespace MusicPlayer
{

    public struct RelativeTime
    {
        public int Value { get; set; }
        public LocalizationKey Unit { get; set; }
    }

    public static class Utilities
    {


        public static async Task<SearchResultItem[]> SearchYoutube(string searchTerm)
        {
            string url = Constants.YoutubeSearchUrl(searchTerm);
            Debug.WriteLine(url);
            HttpClient client = new HttpClient();
            HttpResponseMessage response = await client.GetAsync(url);
            if (response.IsSuccessStatusCode)
            {
                string json = await response.Content.ReadAsStringAsync();
                YouTubeSearchResponse? searchResponse = JsonSerializer.Deserialize<YouTubeSearchResponse>(json);

                if (searchResponse?.Items != null && searchResponse.Items.Count > 0)
                {
                    return searchResponse.Items.ToArray();
                }
                return Array.Empty<SearchResultItem>();
            }
            return [];

        }
        public static RelativeTime ISOToRelativeTime(string isoTime)
        {
            //"2015-10-23T06:54:18Z" -> "9Y Ago"
            DateTime time = DateTime.Parse(isoTime);
            DateTime now = DateTime.Now;
            TimeSpan diff = now - time;
            int value = 0;
            if (diff.TotalDays > 365 * 2)
            {
                value = (int)(diff.TotalDays / 365);
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.yearAgo : LocalizationKey.yearsAgo };
            }
            else if (diff.TotalDays > 365)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.yearsAgo };
            }
            else if (diff.TotalDays > 30 * 2)
            {
                value = (int)(diff.TotalDays / 30);
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.monthAgo : LocalizationKey.monthsAgo };
            }
            else if (diff.TotalDays > 30)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.monthAgo };
            }
            else if (diff.TotalDays > 7 * 2)
            {
                value = (int)(diff.TotalDays / 7);
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.weekAgo : LocalizationKey.weeksAgo };
            }
            else if (diff.TotalDays > 7)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.weekAgo };
            }
            else if (diff.TotalDays > 2)
            {
                value = (int)diff.TotalDays;
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.dayAgo : LocalizationKey.daysAgo };
            }
            else if (diff.TotalDays > 1)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.dayAgo };
            }
            else if (diff.TotalHours > 2)
            {
                value = (int)diff.TotalHours;
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.hourAgo : LocalizationKey.hoursAgo };
            }
            else if (diff.TotalHours > 1)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.hourAgo };
            }
            else
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.hourAgo };
            }


        }





    }
}