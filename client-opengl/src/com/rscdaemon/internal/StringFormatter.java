package com.rscdaemon.internal;

/**
 * An internal tool that eliminates boilerplate code while providing a 
 * centralized means by which to format the string representations of classes 
 * in the project.  String formats can be provided on a per-class basis by 
 * simply defining a system property whose name is the fully qualified 
 * class followed by '.stringformat'.
 * <br>
 * <br>
 * For example, this class could have its 
 * string representation overridden by providing the JVM argument:
 * <pre>
 * -Dcom.rscdaemon.internal.StringFormatter.stringformat="customformat(%1$)"
 * </pre>
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class StringFormatter
{
	/// The <code>StringFormatter</code> of this class
	private final static StringFormatter FORMATTER = 
			new StringFormatter(StringFormatter.class, "default formatter");

	/// The String format of this <code>StringFormatter</code>
	private final String format;
	

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString()
	{
		return FORMATTER.format();
	}
	
	/**
	 * Constructs a <code>StringFormatter</code> with the provided class and 
	 * default format
	 * 
	 * @param clazz the class to create a <code>StringFormatter</code> for
	 * 
	 * @param defaultFormat the default format to use
	 * 
	 * @throws NullPointerException if the provided class is null
	 * 
	 * @throws NullPointerException if the provided default format is null 
	 * and no format is provided through a system property
	 * 
	 * @since 1.0
	 * 
	 */
	public StringFormatter(Class<?> clazz, String defaultFormat)
	{
		String format = System.getProperty(clazz.getName() + ".stringformat");
		if(format == null)
		{
			format = defaultFormat;
		}
		this.format = format;
	}
	
	/**
	 * Generates a String formatted with the provided arguments
	 * 
	 * @param args the arguments to format the String with
	 * 
	 * @return a String formatted with the provided arguments
	 * 
	 * @since 1.0
	 * 
	 */
	public String format(Object... args)
	{
		return String.format(format, args);
	}

	public String getFormat()
	{
		return format;
	}
}
