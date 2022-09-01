package net.imglib2.iterator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class RealIntervalIteratorTests
{
	@Test
	public void realIntervalIteratorTest()
	{
		final double eps = 1e-6;
		final double[] min = new double[] { -1, -1  };
		final double[] max = new double[] { 1 + eps / 2, 1 + eps / 2 };

		final LocalizingRealIntervalIterator it = new LocalizingRealIntervalIterator( 
				min, max, new double[] { 1, 0.5 });

		int N = 0;
		while( it.hasNext() )
		{
			it.fwd();

			int j = ( N - ( N % 3 ) ) / 3;
			assertEquals( (N % 3) - 1, it.getDoublePosition( 0 ), eps );
			assertEquals( -1.0 + 0.5 * j, it.getDoublePosition( 1 ), eps );

			N++;
		}
		assertEquals( 15, N );
	}

	@Test
	public void fillSizeTest()
	{
		final double EPS = 1e-9;
		final double[] min = new double[] { -1, -1 };
		final double[] max = new double[] { 1, 1 }; 

		// test that a single step size is applied to all dimensions
		// and that step size and number of samples per dimension behave the same way
		final LocalizingRealIntervalIterator itSteps = new LocalizingRealIntervalIterator( min, max, 1.0 );
		final LocalizingRealIntervalIterator itNum = LocalizingRealIntervalIterator.createWithSteps( min, max, 3 );

		int N = 0;
		while( itSteps.hasNext() )
		{
			itSteps.fwd();
			itNum.fwd();
			assertArrayEquals( itSteps.positionAsDoubleArray(), itNum.positionAsDoubleArray(), EPS );
			N++;
		}
		assertEquals( 9, N );
	}
	
	@Test
	public void snapTest()
	{
		final double EPS = 1e-9;
		final double[] min = new double[] { -1 };
		final double[] max = new double[] { 1 }; 

		// test that snapping works
		LocalizingRealIntervalIterator it = new LocalizingRealIntervalIterator( min, max, 0.3 );	
		while( it.hasNext() )
		{
			it.fwd();
		}
		assertEquals( 1.0, it.getDoublePosition( 0 ), EPS );

		// test that snapping can be turned off
		it = new LocalizingRealIntervalIterator( min, max, false, 0.3 );	
		while( it.hasNext() )
		{
			it.fwd();
		}
		assertNotEquals( 1.0, it.getDoublePosition( 0 ), EPS );
	}



}
