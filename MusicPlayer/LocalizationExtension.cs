using System.Collections.ObjectModel;
using System.Globalization;

namespace MusicPlayer
{
	[ContentProperty(nameof(Key))]
	public class TranslateExtension : IMarkupExtension<BindingBase>
	{
		public object? Key { get; set; }
		private Collection<object> _parameters;

		public TranslateExtension()
		{
			_parameters = new Collection<object>();
		}

		public Collection<object> Parameters
		{
			get => _parameters;
			set
			{
				if (value is IEnumerable<object> enumerable)
				{
					_parameters = new Collection<object>(enumerable.ToList());
				}
				else
				{
					_parameters = value;
				}
			}
		}

		public BindingBase ProvideValue(IServiceProvider serviceProvider)
		{
			if (Parameters.Count == 0)
			{
				if (Key is Binding keyBinding)
				{
					var binding = new MultiBinding
					{
						Converter = new LocalizationKeyConverter(),
					};
					binding.Bindings.Add(keyBinding);
					return binding;
				}
				return new Binding($"[{Key}]", source: LocalizationResourceManager.Instance);
			}

			var formatBinding = new MultiBinding
			{
				Converter = new LocalizationFormatConverter(),
			};

			// Add translation string
			if (Key is Binding keyBinding2)
			{
				var translationBinding = new MultiBinding
				{
					Converter = new LocalizationKeyConverter(),
				};
				translationBinding.Bindings.Add(keyBinding2);
				formatBinding.Bindings.Add(translationBinding);
			}
			else
			{
				formatBinding.Bindings.Add(new Binding($"[{Key}]", source: LocalizationResourceManager.Instance));
			}

			// Add parameter bindings
			foreach (var parameter in Parameters)
			{
				if (parameter is Binding parameterBinding)
				{
					formatBinding.Bindings.Add(parameterBinding);
				}
				else
				{
					formatBinding.Bindings.Add(new Binding { Source = parameter });
				}
			}

			return formatBinding;
		}

		object IMarkupExtension.ProvideValue(IServiceProvider serviceProvider)
		{
			return ProvideValue(serviceProvider);
		}

	}

	public class LocalizationKeyConverter : IMultiValueConverter
	{
		public object Convert(object[] values, Type targetType, object parameter, CultureInfo culture)
		{
			if (values.Length == 0 || values[0] == null)
				return string.Empty;

			var key = values[0].ToString();
			return key is null ? string.Empty : LocalizationResourceManager.Instance[key];
		}

		public object[] ConvertBack(object value, Type[] targetTypes, object parameter, CultureInfo culture)
		{
			throw new NotImplementedException();
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
