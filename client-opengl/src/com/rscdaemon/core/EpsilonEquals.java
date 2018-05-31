package com.rscdaemon.core;

/**
 * An interface for classes that should be compared with one another with 
 * a certain margin of error to account for floating point inaccuracies.
 * 
 * @author Zilent
 *
 * @param <T> The class derived from this interface (tight restriction)
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Machine_epsilon">
 * http://en.wikipedia.org/wiki/Machine_epsilon</a>
 * 
 */
public interface EpsilonEquals
{
	/**
	 * Determines whether this object equals the provided object with the 
	 * provided error threshold.  Two objects are said to be equal if and 
	 * only if the difference of each dimensional metric is less than or 
	 * equal to the provided epsilon value.
	 * <br>
	 * <br>
	 * <strong>The provided epsilon 
	 * value must be positive, otherwise an exception shall be thrown.</strong>
	 * 
	 * @param other the other object to compare
	 * 
	 * @param epsilon the error threshold
	 * 
	 * @return true if this object equals the provided one, otherwise false
	 * 
	 * @throws IllegalArgumentException if the provided epsilon value is 
	 * negative
	 * 
	 * @throws NullPointerException if the provided other value is null
	 * 
	 * @since 1.0
	 * 
	 */
	boolean equalsWithEpsilon(EpsilonEquals other, float epsilon);
}
