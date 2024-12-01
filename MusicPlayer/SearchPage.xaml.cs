



using System.Diagnostics;
using Material.Components.Maui;

namespace MusicPlayer;

public class SearchListItem()
{
    required public string VideoId { get; set; }
    required public string Title { get; set; }
    required public string Description { get; set; }
    required public string ChannelTitle { get; set; }
    required public RelativeTime PublishTime { get; set; }
    // required public int Views;
    // required public int Likes;
    required public string ThumbnailUrl { get; set; }

}
public partial class SearchPage : ContentView
{
    private List<SearchListItem> SearchResultsList = new List<SearchListItem>();



    public SearchPage()
    {
        InitializeComponent();





    }

    private async void Search(object sender, EventArgs e)
    {
        string searchTerm = SearchField.Text;
        if (string.IsNullOrWhiteSpace(searchTerm))
        {
            return;
        }
        SearchResultItem[] searchResults = await Utilities.SearchYoutube(searchTerm);

        List<SearchListItem> searchResultItems = new List<SearchListItem>();
        foreach (SearchResultItem item in searchResults)
        {
            if (item == null || item.Snippet == null)
            {
                continue;
            }



            SearchListItem newItem = new SearchListItem()
            {
                VideoId = item.Id.Id,
                Title = item.Snippet.Title ?? "",
                Description = item.Snippet.Description ?? "",
                ChannelTitle = item.Snippet.ChannelTitle ?? "",
                PublishTime = Utilities.ISOToRelativeTime(item.Snippet.PublishTime ?? ""),
                ThumbnailUrl = item.Snippet.Thumbnails?.High?.Url ?? item.Snippet.Thumbnails?.Medium?.Url ?? item.Snippet.Thumbnails?.Default?.Url ?? ""
            };
            Debug.WriteLine(newItem.Title);
            searchResultItems.Add(newItem);
        }
        SearchResultsList = searchResultItems;

        DownloadList.ItemsSource = SearchResultsList;


    }

    private void Chip_Clicked(object sender, TouchEventArgs e)
    {
        var chip = (Chip)sender;
        chip.IsSelected = false;
    }




}