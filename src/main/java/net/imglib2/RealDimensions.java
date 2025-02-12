/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2;

import java.util.Arrays;

import net.imglib2.exception.InvalidDimensionsException;
import net.imglib2.exception.InvalidRealDimensionsException;

/**
 * Defines an extent in <em>n</em>-dimensional discrete space.
 *
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 * @author John Bogovic
 */
public interface RealDimensions extends EuclideanSpace
{
	/**
	 * Write this object's dimensions into a double[]
	 *
	 * @param dimensions
	 */
	default void realDimensions( final double[] dimensions )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			dimensions[ d ] = realDimension( d );
	}

	/**
	 * Write this object's dimensions into a {@link RealPositionable}
	 *
	 * @param dimensions
	 */
	default void realDimensions( final RealPositionable dimensions )
	{
		final int n = numDimensions();
		for ( int d = 0; d < n; d++ )
			dimensions.setPosition( realDimension( d ), d );
	}

	/**
	 * Get the size along a particular dimension.
	 *
	 * @param d
	 */
	double realDimension( int d );

	/**
	 * Allocates a new double array with the dimensions of this object.
	 *
	 * Please note that his method allocates a new array each time which
	 * introduces notable overhead in both compute and memory.
	 * If you query it frequently, you should allocate a dedicated array
	 * first and reuse it with {@link #dimensions(double[])}.
	 *
	 * @return the dimensions
	 */
	default double[] dimensionsAsDoubleArray()
	{
		final double[] dims = new double[ numDimensions() ];
		realDimensions( dims );
		return dims;
	}

	/**
	 * Allocates a new {@link Point} with the dimensions of this object.
	 *
	 * Please note that his method allocates a new {@link Point} each time
	 * which introduces notable overhead in both compute and memory.
	 * If you query it frequently, you should allocate a dedicated
	 * {@link Point} first and reuse it with {@link #dimensions(Positionable)}.
	 *
	 * @return the dimensions
	 */
	default RealPoint dimensionsAsRealPoint()
	{
		return new RealPoint( dimensionsAsDoubleArray() );
	}

	/*
	 * -----------------------------------------------------------------------
	 *
	 * Static methods
	 *
	 * -----------------------------------------------------------------------
	 */

	/**
	 * Check whether all entries in {@code dimensions} are positive
	 *
	 * @param dimensions
	 * @return true if all entries in {@code dimension} are positive, false
	 *         otherwise
	 */
	static boolean allPositive( final double... dimensions )
	{
		for ( final double d : dimensions )
			if ( d < 0 )
				return false;
		return true;
	}

	/**
	 * Check whether all entries in {@code dimensions} are positive
	 *
	 * @param dimensions
	 * @return true if all entries in {@code dimension} are positive, false
	 *         otherwise
	 */
	static boolean allPositive( final float... dimensions )
	{
		for ( final float d : dimensions )
			if ( d < 1 )
				return false;
		return true;
	}

	/**
	 * Check that all entries in {@code dimensions} are positive
	 *
	 * @param dimensions
	 * @return {@code dimensions}
	 * @throws InvalidDimensionsException
	 *             if any of {@code dimensions} is not positive (zero or
	 *             negative).
	 */
	static double[] verifyAllPositive( final double... dimensions ) throws InvalidRealDimensionsException
	{
		if ( !RealDimensions.allPositive( dimensions ) )
			throw new InvalidRealDimensionsException(
					dimensions,
					"Expected only positive dimensions but got: " + Arrays.toString( dimensions ) );
		return dimensions;
	}

	/**
	 * Check that all entries in {@code dimensions} are positive
	 *
	 * @param dimensions
	 * @return {@code dimensions}
	 * @throws InvalidDimensionsException
	 *             if any of {@code dimensions} is not positive (zero or
	 *             negative).
	 */
	static float[] verifyAllPositive( final float... dimensions ) throws InvalidRealDimensionsException
	{
		if ( !RealDimensions.allPositive( dimensions ) )
			throw new InvalidRealDimensionsException(
					dimensions,
					"Expected only positive dimensions but got: " + Arrays.toString( dimensions ) );
		return dimensions;
	}

	/**
	 * Verify that {@code dimensions} is not null or empty, and that all
	 * dimensions are positive. Throw {@link InvalidDimensionsException} otherwise.
	 *
	 * @param dimensions
	 *            to be verified.
	 * @return {@code dimensions} if successfully verified.
	 * @throws IllegalArgumentException
	 *             if {@code dimensions == null} or
	 *             {@code dimensions.length == 0} or any dimensions is zero or
	 *             negative.
	 */
	static double[] verify( final double... dimensions ) throws InvalidRealDimensionsException
	{
		if ( dimensions == null )
			throw new InvalidRealDimensionsException( dimensions, "Dimensions are null." );

		if ( dimensions.length == 0 )
			throw new InvalidRealDimensionsException( dimensions, "Dimensions are zero length." );

		return verifyAllPositive( dimensions );
	}

	/**
	 * Verify that {@code dimensions} is not null or empty, and that all
	 * dimensions are positive. Throw {@link InvalidDimensionsException} otherwise.
	 *
	 * @param dimensions
	 *            to be verified.
	 * @return {@code dimensions} if successfully verified.
	 * @throws IllegalArgumentException
	 *             if {@code dimensions == null} or
	 *             {@code dimensions.length == 0} or any dimensions is zero or
	 *             negative.
	 */
	static float[] verify( final float... dimensions ) throws InvalidRealDimensionsException
	{
		if ( dimensions == null )
			throw new InvalidRealDimensionsException( dimensions, "Dimensions are null." );

		if ( dimensions.length == 0 )
			throw new InvalidRealDimensionsException( dimensions, "Dimensions are zero length." );

		return verifyAllPositive( dimensions );
	}
}
