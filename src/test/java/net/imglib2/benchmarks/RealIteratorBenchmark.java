package net.imglib2.benchmarks;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealPoint;
import net.imglib2.converter.Converters;
import net.imglib2.iterator.LocalizingRealIntervalIterator;
import net.imglib2.util.Intervals;
import net.imglib2.util.Localizables;
import net.imglib2.view.Views;

@State( Scope.Thread )
@Fork( 1 )
public class RealIteratorBenchmark {

	private LocalizingRealIntervalIterator lit;
	private Iterator< RealPoint > cnv;

	public static void main(String... args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(RealIteratorBenchmark.class.getSimpleName())
                .forks(1)
				.warmupIterations( 4 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 2000 ) )
				.measurementTime( TimeValue.milliseconds( 5000 ) )
                .build();

        new Runner(opt).run();
	}

	@Setup( Level.Iteration )
	public void allocate()
	{
		FinalRealInterval itvl = Intervals.createMinMaxReal( 0, 0, 0, 500, 500, 500 );
		lit = LocalizingRealIntervalIterator.createWithSteps( itvl, 100 );
		cnv = usingConverters( itvl, 100 );
	}

	public static Iterator< RealPoint > usingConverters( RealInterval interval, int... numSteps )
	{
		final int nd = interval.numDimensions();
		final double[] steps = LocalizingRealIntervalIterator.stepsFromSamples( interval, LocalizingRealIntervalIterator.fillWithLast( nd, numSteps ) );
		final RandomAccessibleInterval< Localizable > samples = Localizables.randomAccessibleInterval( 
			new FinalInterval( Arrays.stream( steps ).mapToLong( x -> (long)x ).toArray() ));

		RandomAccessibleInterval< RealPoint > positions = Converters.convert2( samples, (x,y) -> {
			for( int i = 0; i < nd; i++ )
				y.setPosition(
						interval.realMin( i ) + steps[ i ] * x.getDoublePosition( i ),
						i );
		}, () -> new RealPoint(nd));

		return Views.flatIterable( positions ).iterator();
	}


	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.NANOSECONDS )
	public void iterator()
	{
		while( lit.hasNext() )
			lit.fwd();
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.NANOSECONDS )
	public void converter()
	{
		while( cnv.hasNext() )
			cnv.next();
	}


}