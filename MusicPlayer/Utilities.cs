using System.Diagnostics;
using System.Text.Json;

namespace MusicPlayer
{

    public struct RelativeTime
    {
        public int Value { get; set; }
        public string Unit { get; set; }
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
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.year.ToString() : LocalizationKey.years.ToString() };
            }
            else if (diff.TotalDays > 365)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.year.ToString() };
            }
            else if (diff.TotalDays > 30 * 2)
            {
                value = (int)(diff.TotalDays / 30);
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.month.ToString() : LocalizationKey.months.ToString() };
            }
            else if (diff.TotalDays > 30)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.month.ToString() };
            }
            else if (diff.TotalDays > 7 * 2)
            {
                value = (int)(diff.TotalDays / 7);
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.week.ToString() : LocalizationKey.weeks.ToString() };
            }
            else if (diff.TotalDays > 7)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.week.ToString() };
            }
            else if (diff.TotalDays > 2)
            {
                value = (int)diff.TotalDays;
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.day.ToString() : LocalizationKey.days.ToString() };
            }
            else if (diff.TotalDays > 1)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.day.ToString() };
            }
            else if (diff.TotalHours > 2)
            {
                value = (int)diff.TotalHours;
                return new RelativeTime { Value = value, Unit = value == 1 ? LocalizationKey.hour.ToString() : LocalizationKey.hours.ToString() };
            }
            else if (diff.TotalHours > 1)
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.hour.ToString() };
            }
            else
            {
                return new RelativeTime { Value = 1, Unit = LocalizationKey.hour.ToString() };
            }


        }





    }
}