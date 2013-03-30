package be.Balor.Tools.Compatibility.Reflect.Fuzzy;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import be.Balor.Tools.Compatibility.Reflect.MethodInfo;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Determine if a given class implements a given fuzzy (duck typed) contract.
 * 
 * @author Kristian
 */
public class FuzzyClassContract extends AbstractFuzzyMatcher<Class<?>> {
	private final ImmutableList<AbstractFuzzyMatcher<Field>> fieldContracts;
	private final ImmutableList<AbstractFuzzyMatcher<MethodInfo>> methodContracts;
	private final ImmutableList<AbstractFuzzyMatcher<MethodInfo>> constructorContracts;

	/**
	 * Represents a class contract builder.
	 * 
	 * @author Kristian
	 * 
	 */
	public static class Builder {
		private final List<AbstractFuzzyMatcher<Field>> fieldContracts = Lists
				.newArrayList();
		private final List<AbstractFuzzyMatcher<MethodInfo>> methodContracts = Lists
				.newArrayList();
		private final List<AbstractFuzzyMatcher<MethodInfo>> constructorContracts = Lists
				.newArrayList();

		/**
		 * Add a new field contract.
		 * 
		 * @param matcher
		 *            - new field contract.
		 * @return This builder, for chaining.
		 */
		public Builder field(final AbstractFuzzyMatcher<Field> matcher) {
			fieldContracts.add(matcher);
			return this;
		}

		/**
		 * Add a new field contract via a builder.
		 * 
		 * @param builder
		 *            - builder for the new field contract.
		 * @return This builder, for chaining.
		 */
		public Builder field(final FuzzyFieldContract.Builder builder) {
			return field(builder.build());
		}

		/**
		 * Add a new method contract.
		 * 
		 * @param matcher
		 *            - new method contract.
		 * @return This builder, for chaining.
		 */
		public Builder method(final AbstractFuzzyMatcher<MethodInfo> matcher) {
			methodContracts.add(matcher);
			return this;
		}

		/**
		 * Add a new method contract via a builder.
		 * 
		 * @param builder
		 *            - builder for the new method contract.
		 * @return This builder, for chaining.
		 */
		public Builder method(final FuzzyMethodContract.Builder builder) {
			return method(builder.build());
		}

		/**
		 * Add a new constructor contract.
		 * 
		 * @param matcher
		 *            - new constructor contract.
		 * @return This builder, for chaining.
		 */
		public Builder constructor(
				final AbstractFuzzyMatcher<MethodInfo> matcher) {
			constructorContracts.add(matcher);
			return this;
		}

		/**
		 * Add a new constructor contract via a builder.
		 * 
		 * @param builder
		 *            - builder for the new constructor contract.
		 * @return This builder, for chaining.
		 */
		public Builder constructor(final FuzzyMethodContract.Builder builder) {
			return constructor(builder.build());
		}

		public FuzzyClassContract build() {
			Collections.sort(fieldContracts);
			Collections.sort(methodContracts);
			Collections.sort(constructorContracts);

			// Construct a new class matcher
			return new FuzzyClassContract(ImmutableList.copyOf(fieldContracts),
					ImmutableList.copyOf(methodContracts),
					ImmutableList.copyOf(constructorContracts));
		}
	}

	/**
	 * Construct a new fuzzy class contract builder.
	 * 
	 * @return A new builder.
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructs a new fuzzy class contract with the given contracts.
	 * 
	 * @param fieldContracts
	 *            - field contracts.
	 * @param methodContracts
	 *            - method contracts.
	 * @param constructorContracts
	 *            - constructor contracts.
	 */
	private FuzzyClassContract(
			final ImmutableList<AbstractFuzzyMatcher<Field>> fieldContracts,
			final ImmutableList<AbstractFuzzyMatcher<MethodInfo>> methodContracts,
			final ImmutableList<AbstractFuzzyMatcher<MethodInfo>> constructorContracts) {
		super();
		this.fieldContracts = fieldContracts;
		this.methodContracts = methodContracts;
		this.constructorContracts = constructorContracts;
	}

	/**
	 * Retrieve an immutable list of every field contract.
	 * <p>
	 * This list is ordered in descending order of priority.
	 * 
	 * @return List of every field contract.
	 */
	public ImmutableList<AbstractFuzzyMatcher<Field>> getFieldContracts() {
		return fieldContracts;
	}

	/**
	 * Retrieve an immutable list of every method contract.
	 * <p>
	 * This list is ordered in descending order of priority.
	 * 
	 * @return List of every method contract.
	 */
	public ImmutableList<AbstractFuzzyMatcher<MethodInfo>> getMethodContracts() {
		return methodContracts;
	}

	/**
	 * Retrieve an immutable list of every constructor contract.
	 * <p>
	 * This list is ordered in descending order of priority.
	 * 
	 * @return List of every constructor contract.
	 */
	public ImmutableList<AbstractFuzzyMatcher<MethodInfo>> getConstructorContracts() {
		return constructorContracts;
	}

	@Override
	protected int calculateRoundNumber() {
		// Find the highest round number
		return combineRounds(
				findHighestRound(fieldContracts),
				combineRounds(findHighestRound(methodContracts),
						findHighestRound(constructorContracts)));
	}

	private <T> int findHighestRound(
			final Collection<AbstractFuzzyMatcher<T>> list) {
		int highest = 0;

		// Go through all the elements
		for (final AbstractFuzzyMatcher<T> matcher : list) {
			highest = combineRounds(highest, matcher.getRoundNumber());
		}
		return highest;
	}

	@Override
	public boolean isMatch(final Class<?> value, final Object parent) {
		final FuzzyReflection reflection = FuzzyReflection.fromClass(value,
				true);

		// Make sure all the contracts are valid
		return processContracts(reflection.getFields(), value, fieldContracts)
				&& processContracts(
						MethodInfo.fromMethods(reflection.getMethods()), value,
						methodContracts)
				&& processContracts(MethodInfo.fromConstructors(value
						.getDeclaredConstructors()), value,
						constructorContracts);
	}

	private <T> boolean processContracts(final Collection<T> values,
			final Class<?> parent, final List<AbstractFuzzyMatcher<T>> matchers) {
		final boolean[] accepted = new boolean[matchers.size()];
		int count = accepted.length;

		// Process every value in turn
		for (final T value : values) {
			final int index = processValue(value, parent, accepted, matchers);

			// See if this worked
			if (index >= 0) {
				accepted[index] = true;
				count--;
			}

			// Break early
			if (count == 0) {
				return true;
			}
		}
		return count == 0;
	}

	private <T> int processValue(final T value, final Class<?> parent,
			final boolean accepted[],
			final List<AbstractFuzzyMatcher<T>> matchers) {
		// The order matters
		for (int i = 0; i < matchers.size(); i++) {
			if (!accepted[i]) {
				final AbstractFuzzyMatcher<T> matcher = matchers.get(i);

				// Mark this as detected
				if (matcher.isMatch(value, parent)) {
					return i;
				}
			}
		}

		// Failure
		return -1;
	}

	@Override
	public String toString() {
		final Map<String, Object> params = Maps.newLinkedHashMap();

		if (fieldContracts.size() > 0) {
			params.put("fields", fieldContracts);
		}
		if (methodContracts.size() > 0) {
			params.put("methods", methodContracts);
		}
		if (constructorContracts.size() > 0) {
			params.put("constructors", constructorContracts);
		}
		return "{\n  " + Joiner.on(", \n  ").join(params.entrySet()) + "\n}";
	}
}
