/*
 * Copyright 2015-2018 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.api;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.junit.jupiter.api.DisplayNameGenerator.parameterTypesAsString;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.apiguardian.api.API;
import org.junit.platform.commons.util.Preconditions;

/**
 * {@code @DisplayNameGeneration} is used to declare a custom display name
 * generator for the annotated test class.
 *
 * <p>Users may select either a pre-defined {@link Style} or supply a
 * custom {@link DisplayNameGenerator} implementation.
 *
 * @since 5.4
 * @see DisplayName
 * @see DisplayNameGenerator
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@API(status = EXPERIMENTAL, since = "5.4")
public @interface DisplayNameGeneration {

	/**
	 * Custom display name generator.
	 *
	 * @return custom display name generator implementation or {@link DisplayNameGenerator}
	 *         to use the {@link Style} provided by the {@link #value()} property
	 */
	Class<? extends DisplayNameGenerator> generator() default DisplayNameGenerator.class;

	/**
	 * The pre-defined style to use.
	 *
	 * @return the style to use, can be overridden by a custom display
	 * name {@link #generator()} implementation
	 */
	Style value() default Style.DEFAULT;

	/**
	 * Pre-defined {@link DisplayNameGenerator} implementations.
	 */
	enum Style implements DisplayNameGenerator {
		/**
		 * Default display name generator.
		 *
		 * <p>The implementation matches the published behaviour when Jupiter 5.0.0
		 * was released.
		 */
		DEFAULT {
			@Override
			public String generateDisplayNameForClass(Class<?> testClass) {
				Preconditions.notNull(testClass, "Test class must not be null");
				String name = testClass.getName();
				int lastDot = name.lastIndexOf('.');
				return name.substring(lastDot + 1);
			}

			@Override
			public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
				Preconditions.notNull(nestedClass, "Nested test class must not be null");
				return nestedClass.getSimpleName();
			}

			@Override
			public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
				Preconditions.notNull(testClass, "Test class must not be null");
				Preconditions.notNull(testMethod, "Test method must not be null");
				return testMethod.getName() + parameterTypesAsString(testMethod);
			}
		},

		/**
		 * Replace all underscore characters with spaces.
		 *
		 * <p>The {@code UNDERSCORE} style replaces all underscore characters ({@code '_'})
		 * found in class and method names with a space character: {@code ' '}.
		 */
		UNDERSCORE {
			@Override
			public String generateDisplayNameForClass(Class<?> testClass) {
				return replaceUnderscores(DEFAULT.generateDisplayNameForClass(testClass));
			}

			@Override
			public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
				return replaceUnderscores(DEFAULT.generateDisplayNameForNestedClass(nestedClass));
			}

			@Override
			public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
				Preconditions.notNull(testClass, "Test class must not be null");
				Preconditions.notNull(testMethod, "Test method must not be null");
				// don't replace underscores in parameter type names
				return replaceUnderscores(testMethod.getName()) + parameterTypesAsString(testMethod);
			}

			private String replaceUnderscores(String name) {
				return name.replace('_', ' ');
			}
		}

	}

}
