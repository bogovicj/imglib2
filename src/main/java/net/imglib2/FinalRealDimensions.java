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

import net.imglib2.util.Intervals;

import java.util.Arrays;
import java.util.StringJoiner;

public final class FinalRealDimensions implements RealDimensions
{
	final double[] dimensions;

	/**
	 * Protected constructor that can re-use the passed position array.
	 *
	 * @param dimensions
	 *            array used to store the position.
	 * @param copy
	 *            flag indicating whether position array should be duplicated.
	 */
	protected FinalRealDimensions( final double[] dimensions, final boolean copy )
	{
		if ( copy )
			this.dimensions = dimensions.clone();
		else
			this.dimensions = dimensions;
	}

	/**
	 * Create a FinalDimensions with a defined size
	 *
	 * @param dimensions
	 *            the size
	 */
	public FinalRealDimensions( final double... dimensions )
	{
		this( dimensions, true );
	}

	/**
	 * Create a FinalDimensions with a defined size
	 *
	 * @param dimensions
	 *            the size
	 */
	public FinalRealDimensions( final RealDimensions dimensions )
	{
		this( dimensions.dimensionsAsDoubleArray(), false );
	}

	@Override
	public int numDimensions()
	{
		return dimensions.length;
	}

	@Override
	public void realDimensions( final double[] dims )
	{
		for ( int d = 0; d < dims.length; ++d )
			dims[ d ] = this.dimensions[ d ];
	}

	@Override
	public double realDimension( final int d )
	{
		return dimensions[ d ];
	}

	@Override
	public String toString()
	{
		final StringJoiner joiner = new StringJoiner( "x" );
		for ( int d = 0; d < numDimensions(); d++ )
			joiner.add( Double.toString( realDimension( d ) ) );
		return joiner.toString();
	}

	@Override
	public boolean equals( final Object obj )
	{
		return obj instanceof FinalRealDimensions &&
				Intervals.equalRealDimensions( this, (FinalRealDimensions) obj );
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode( dimensions );
	}

	/**
	 * Create a FinalRealDimensions object that stores its coordinates in the
	 * provided array.
	 *
	 * @param dimensions
	 *            array to use for storing the dimensions.
	 */
	public static FinalRealDimensions wrap( final double[] dimensions )
	{
		return new FinalRealDimensions( dimensions, false );
	}

}
