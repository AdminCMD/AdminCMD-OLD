package be.Balor.Tools.Compatibility.Reflect.Fuzzy;

import com.google.common.primitives.Ints;

/**
 * Represents a matcher for fields, methods, constructors and classes.
 * <p>
 * This class should ideally never expose mutable state. Its round number must
 * be immutable.
 * 
 * @author Kristian
 */
public abstract class AbstractFuzzyMatcher<T> implements
		Comparable<AbstractFuzzyMatcher<T>> {
	private Integer roundNumber;

	/**
	 * Determine if the given value is a match.
	 * 
	 * @param value
	 *            - the value to match.
	 * @param parent
	 *            - the parent container, or NULL if this value is the root.
	 * @return TRUE if it is a match, FALSE otherwise.
	 */
	public abstract boolean isMatch(T value, Object parent);

	/**
	 * Calculate the round number indicating when this matcher should be
	 * applied.
	 * <p>
	 * Matchers with a lower round number are applied before matchers with a
	 * higher round number.
	 * <p>
	 * By convention, this round number should be negative, except for zero in
	 * the case of a matcher that accepts any value. A good implementation
	 * should return the inverted tree depth (class hierachy) of the least
	 * specified type used in the matching. Thus {@link Integer} will have a
	 * lower round number than {@link Number}.
	 * 
	 * @return A number (positive or negative) that is used to order matchers.
	 */
	protected abstract int calculateRoundNumber();

	/**
	 * Retrieve the cached round number. This should never change once
	 * calculated.
	 * <p>
	 * Matchers with a lower round number are applied before matchers with a
	 * higher round number.
	 * 
	 * @return The round number.
	 * @see {@link #calculateRoundNumber()}
	 */
	public final int getRoundNumber() {
		if (roundNumber == null) {
			return roundNumber = calculateRoundNumber();
		} else {
			return roundNumber;
		}
	}

	/**
	 * Combine two round numbers by taking the highest non-zero number, or
	 * return zero.
	 * 
	 * @param roundA
	 *            - the first round number.
	 * @param roundB
	 *            - the second round number.
	 * @return The combined round number.
	 */
	protected final int combineRounds(final int roundA, final int roundB) {
		if (roundA == 0) {
			return roundB;
		} else if (roundB == 0) {
			return roundA;
		} else {
			return Math.max(roundA, roundB);
		}
	}

	@Override
	public int compareTo(final AbstractFuzzyMatcher<T> obj) {
		if (obj instanceof AbstractFuzzyMatcher) {
			final AbstractFuzzyMatcher<?> matcher = obj;
			return Ints.compare(getRoundNumber(), matcher.getRoundNumber());
		}
		// No match
		return -1;
	}

	/**
	 * Create a fuzzy matcher that returns the opposite result of the current
	 * matcher.
	 * 
	 * @return An inverted fuzzy matcher.
	 */
	public AbstractFuzzyMatcher<T> inverted() {
		return new AbstractFuzzyMatcher<T>() {
			@Override
			public boolean isMatch(final T value, final Object parent) {
				return !AbstractFuzzyMatcher.this.isMatch(value, parent);
			}

			@Override
			protected int calculateRoundNumber() {
				return -2;
			}
		};
	}

	/**
	 * Require that this and the given matcher be TRUE.
	 * 
	 * @param other
	 *            - the other fuzzy matcher.
	 * @return A combined fuzzy matcher.
	 */
	public AbstractFuzzyMatcher<T> and(final AbstractFuzzyMatcher<T> other) {
		return new AbstractFuzzyMatcher<T>() {
			@Override
			public boolean isMatch(final T value, final Object parent) {
				// They both have to be true
				return AbstractFuzzyMatcher.this.isMatch(value, parent)
						&& other.isMatch(value, parent);
			}

			@Override
			protected int calculateRoundNumber() {
				return combineRounds(
						AbstractFuzzyMatcher.this.getRoundNumber(),
						other.getRoundNumber());
			}
		};
	}

	/**
	 * Require that either this or the other given matcher be TRUE.
	 * 
	 * @param other
	 *            - the other fuzzy matcher.
	 * @return A combined fuzzy matcher.
	 */
	public AbstractFuzzyMatcher<T> or(final AbstractFuzzyMatcher<T> other) {
		return new AbstractFuzzyMatcher<T>() {
			@Override
			public boolean isMatch(final T value, final Object parent) {
				// Either can be true
				return AbstractFuzzyMatcher.this.isMatch(value, parent)
						|| other.isMatch(value, parent);
			}

			@Override
			protected int calculateRoundNumber() {
				return combineRounds(
						AbstractFuzzyMatcher.this.getRoundNumber(),
						other.getRoundNumber());
			}
		};
	}
}