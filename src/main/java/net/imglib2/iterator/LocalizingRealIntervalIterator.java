/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.iterator;

import net.imglib2.AbstractRealInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Iterator;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Util;

/**
 * Use this class to iterate a virtual {@link RealInterval} in flat order, that
 * is: with the first dimension varying most quickly and the last dimension
 * varying most slowly. This is useful for iterating an arbitrary real interval
 * in a defined order. 
 *
 * @author John Bogovic
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LocalizingRealIntervalIterator extends AbstractRealInterval implements Iterator, RealLocalizable
{
	final protected double[] step;

	final protected double[] location;

	protected double eps = 1e-9;

	/**
	 * Iterates an {@link RealInterval} using the provided step size along each dimension.
	 *
	 * @param interval the real interval
	 * @param step iteration step
	 */
	public LocalizingRealIntervalIterator( final RealInterval interval, final double... step )
	{
		this( interval, true, step );
	}

	/**
	 * Iterates an {@link RealInterval} with given <em>min</em> and <em>max</em> the
	 * the provided step size along each dimension.
	 *
	 * @param min real interval min
	 * @param max real interval max
	 * @param step iteration steps
	 */
	public LocalizingRealIntervalIterator( final double[] min, final double[] max, final double... step )
	{
		this( min, max, true, step );
	}

	/**
	 * Iterates an {@link RealInterval} using the provided step size along each dimension.
	 *
	 * @param interval the real interval
	 * @param snapToWidth adjust steps to be an integer multiple of interval width
	 * @param step iteration step
	 */
	public LocalizingRealIntervalIterator( final RealInterval interval, boolean snapToWidth, final double... step )
	{
		super( interval );
		this.step = snapToWidth ? adjustSteps( this, fillWithLast( numDimensions(), step )) : fillWithLast( numDimensions(), step );
		this.location = new double[ interval.numDimensions() ];
		reset();
	}

	/**
	 * Iterates an {@link RealInterval} with given <em>min</em> and <em>max</em> the
	 * the provided step size along each dimension.
	 *
	 * @param min real interval min
	 * @param max real interval max
	 * @param snapToWidth adjust steps to be an integer multiple of interval width
	 * @param step iteration steps
	 */
	public LocalizingRealIntervalIterator( final double[] min, final double[] max, boolean snapToWidth, final double... step )
	{
		super( min, max );
		this.step = snapToWidth ? adjustSteps( this, fillWithLast( numDimensions(), step )) : fillWithLast( numDimensions(), step );
		this.location = new double[ numDimensions() ];
		reset();
	}

	/**
	 * Iterates an {@link RealInterval} using the given number of samples per dimension.
	 *
	 * @param interval the real interval
	 * @param numSteps the number of steps per dimension
	 */
	public static LocalizingRealIntervalIterator createWithSteps( final RealInterval interval, final int... numSteps )
	{
		return new LocalizingRealIntervalIterator( interval, stepsFromSamples( interval, numSteps ));
	}
	
	/**
	 * Iterates an {@link RealInterval} using the given number of samples per dimension.
	 *
	 * @param interval the real interval
	 * @param numSteps the number of steps per dimension
	 */
	public static LocalizingRealIntervalIterator createWithSteps( final double[] min, final double[] max, final int... numSteps )
	{
		final FinalRealInterval itvl = new FinalRealInterval( min, max );
		return new LocalizingRealIntervalIterator( itvl, stepsFromSamples( itvl, numSteps ));
	}
	
	public void setEpsilon( final double eps )
	{
		this.eps = eps;
	}

	@Override
	public void reset()
	{
		realMin( location );
		location[ 0 ] -= step[ 0 ];
	}

	@Override
	public boolean hasNext()
	{
		for( int d = 0; d < numDimensions(); d++ )
			if( (location[ d ] + step[ d ]) <= realMax( d ) + eps )
			{
				return true;
			}

		return false;
	}

	@Override
	public String toString()
	{
		return Util.printCoordinates( this );
	}

	@Override
	public void localize(float[] position)
	{
		for( int d = 0; d < position.length; d++ )
			position[ d ] = (float)location[ d ];
	}

	@Override
	public void localize(double[] position)
	{
		System.arraycopy( location, 0, position, 0, position.length );
	}

	@Override
	public float getFloatPosition(int d)
	{
		return (float) location[ d ];
	}

	@Override
	public double getDoublePosition(int d)
	{
		return location[ d ];
	}

	@Override
	public void jumpFwd(long steps)
	{
		for( int i = 0; i < steps; i++ )
			fwd();
	}

	@Override
	public void fwd()
	{
		for( int d = 0; d < numDimensions(); d++ )
		{
			fwdDim( d );
			if( location[ d ] <= realMax(d) + eps)
				break;
			else
				location[ d ] = realMin( d );
		}
	}

	private void fwdDim( final int d )
	{
		location[ d ] += step[ d ];
	}

	/**
	 * Returns step sizes per dimension such that there are numSamples[i] steps for the ith dimension.
	 * <p>
	 * numSamples may be of length less than itvl.numDimensions(), in which case this method uses
	 * the last value (highest index) of numSamplesa for all subsequent dimensions. For example,
	 * passing a 3D interval, but numSamples [32] will use 32 samples for all dimensions.
	 * 
	 * @param interval the interval
	 * @param numSamples the number of steps per dimension
	 * @return step size
	 */
	public static double[] stepsFromSamples( final RealInterval interval, final int... numSamples )
	{
		final int nd = interval.numDimensions();
		double[] steps = new double[ nd ];
		for( int i = 0; i < nd; i++ )
		{
			final int j = i >= numSamples.length ? numSamples.length - 1 : i;
			steps[i] = (interval.realMax(i) - interval.realMin(i)) / ( numSamples[ j ] - 1 );
		}
		return steps;
	}
	

	/**
	 * Returns step sizes that are as close as possible to the provided steps, but
	 * are an whole number multiple of the interval width.
	 * 
	 * @param interval the interval
	 * @param steps the step sizes
	 * @return new step sizes
	 */
	public static double[] adjustSteps( final RealInterval interval, final double... steps )
	{
		final int nd = interval.numDimensions();
		final double[] stepsOut = new double[ nd ];
		for( int i = 0; i < nd; i++ )
		{
			final double w = interval.realMax(i) - interval.realMin(i);
			stepsOut[i] = w / Math.round( w / steps[i] );
		}
		return stepsOut;
	}

	/**
	 * Returns an array of size at least N, containing the given values.
	 * If the length of vales is >= N this returns values directly,
	 * otherwise returns a new array of length N whose where the last (highest index) of
	 * values is repeated.
	 * 
	 * @param N desired size
	 * @param vlues
	 * @return filled values
	 */
	public static double[] fillWithLast( final int N, final double... values )
	{
		if( values.length >= N )
			return values;

		final double[] stepsOut = new double[ N ];
		for( int i = 0; i < N; i++ )
		{
			final int j = i >= values.length ? values.length - 1 : i;
			stepsOut[i] = values[j];
		}

		return stepsOut;
	}

	/**
	 * Returns an array of size at least N, containing the given values.
	 * If the length of vales is >= N this returns values directly,
	 * otherwise returns a new array of length N whose where the last (highest index) of
	 * values is repeated.
	 * 
	 * @param N desired size
	 * @param vlues
	 * @return filled values
	 */
	public static int[] fillWithLast( final int N, final int... values )
	{
		if( values.length >= N )
			return values;

		final int[] stepsOut = new int[ N ];
		for( int i = 0; i < N; i++ )
		{
			final int j = i >= values.length ? values.length - 1 : i;
			stepsOut[i] = values[j];
		}

		return stepsOut;
	}

}
