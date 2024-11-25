using System.Globalization;

namespace MusicPlayer
{
	[ContentProperty(nameof(Key))]
	public class TranslateExtension : IMarkupExtension<BindingBase>
	{
		public LocalizationKey Key { get; set; }
		public object[] Parameters { get; set; } = Array.Empty<object>();

		public BindingBase ProvideValue(IServiceProvider serviceProvider)
		{
			if (Parameters.Length == 0)
			{
				return new Binding($"[{Key}]", source: LocalizationResourceManager.Instance);
			}

			var binding = new MultiBinding
			{
				Converter = new LocalizationFormatConverter(),
				Bindings = { new Binding($"[{Key}]", source: LocalizationResourceManager.Instance) }
			};

			// Add parameter bindings
			foreach (var parameter in Parameters)
			{
				if (parameter is Binding parameterBinding)
				{
					binding.Bindings.Add(parameterBinding);
				}
				else
				{
					binding.Bindings.Add(new Binding { Source = parameter });
				}
			}

			return binding;
		}

		object IMarkupExtension.ProvideValue(IServiceProvider serviceProvider)
		{
			return ProvideValue(serviceProvider);
		}

	}
	public class LocalizationFormatConverter : IMultiValueConverter
	{
		public object Convert(object[] values, Type targetType, object parameter, CultureInfo culture)
		{
			if (values.Length < 2 || values[0] == null)
				return string.Empty;

			string format = values[0]?.ToString() ?? string.Empty;
			var parameters = values.Skip(1).ToArray();
			return string.Format(format, parameters);
		}

		public object[] ConvertBack(object value, Type[] targetTypes, object parameter, CultureInfo culture)
		{
			throw new NotImplementedException();
		}

	}
}
