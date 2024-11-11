using CommunityToolkit.Maui.Storage;
using Microsoft.Maui.ApplicationModel;
using Microsoft.Maui.Controls;
using Microsoft.Maui.Storage;
using System;
using System.Globalization;
using System.Windows.Input;

namespace MusicPlayer;

public partial class SettingsPage : ContentPage
{
	public static readonly BindableProperty MusicDirectoryProperty =
		BindableProperty.Create(nameof(MusicDirectory), typeof(string), typeof(SettingsPage), null,
			propertyChanged: (bindable, oldValue, newValue) =>
			{
				if (bindable is SettingsPage page)
					page.DirectoryLabel.Text = newValue?.ToString() ?? "No directory selected";
			});

	public string MusicDirectory
	{
		get => (string)GetValue(MusicDirectoryProperty);
		set => SetValue(MusicDirectoryProperty, value);
	}

	public SettingsPage()
	{
		InitializeComponent();
		LocalizationResourceManager.Instance.PropertyChanged += (_, _) => { };

	}



	private async void OnLanguageClicked(object sender, EventArgs e)
	{

	}

	private async void OnSelectDirectoryClicked(object sender, EventArgs e)
	{
	}
}