using System.Collections.ObjectModel;
using System.Globalization;

namespace MusicPlayer
{
	[ContentProperty(nameof(Key))]
	public class TranslateExtension : IMarkupExtension<BindingBase>
	{
		public object? Key { get; set; }
		public Collection<object> Parameters { get; set; } = new();

		public BindingBase ProvideValue(IServiceProvider serviceProvider)
		{
			if (Parameters.Count == 0)
			{
				return Key is Binding keyBinding
					? new MultiBinding { Converter = new LocalizationKeyConverter(), Bindings = { keyBinding } }
					: new Binding($"[{Key}]", source: LocalizationResourceManager.Instance);
			}

			var formatBinding = new MultiBinding
			{
				Converter = new LocalizationFormatConverter(),
				Bindings =
				{
					Key is Binding keyBinding2
						? new MultiBinding { Converter = new LocalizationKeyConverter(), Bindings = { keyBinding2 } }
						: new Binding($"[{Key}]", source: LocalizationResourceManager.Instance)
				}
			};

			foreach (var parameter in Parameters)
			{
				formatBinding.Bindings.Add(parameter is Binding paramBinding ? paramBinding : new Binding { Source = parameter });
			}

			return formatBinding;
		}

		object IMarkupExtension.ProvideValue(IServiceProvider serviceProvider) => ProvideValue(serviceProvider);
	}

	public class LocalizationKeyConverter : IMultiValueConverter
	{
		public object Convert(object[] values, Type targetType, object parameter, CultureInfo culture) =>
			(values.Length > 0 && values[0] != null)
				? LocalizationResourceManager.Instance[values[0].ToString() ?? string.Empty]
				: string.Empty;

		public object[] ConvertBack(object value, Type[] targetTypes, object parameter, CultureInfo culture) =>
			throw new NotImplementedException();
	}

	public class LocalizationFormatConverter : IMultiValueConverter
	{
		public object Convert(object[] values, Type targetType, object parameter, CultureInfo culture) =>
			(values.Length >= 2 && values[0] != null)
				? string.Format(values[0].ToString() ?? string.Empty, values.Skip(1).ToArray())
				: string.Empty;

		public object[] ConvertBack(object value, Type[] targetTypes, object parameter, CultureInfo culture) =>
			throw new NotImplementedException();
	}
}
