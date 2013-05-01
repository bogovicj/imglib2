package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;

public class OtherOps
{

	public static final class AddOpOld< T extends NumericType< T > > implements BinaryOperation< T, T, T >
	{
		@Override
		public T compute( final T input1, final T input2, final T output )
		{
			output.set( input1 );
			output.add( input2 );
			return output;
		}

		@Override
		public AddOpOld< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final class SubOpOld< T extends NumericType< T > > implements BinaryOperation< T, T, T >
	{
		@Override
		public T compute( final T input1, final T input2, final T output )
		{
			output.set( input1 );
			output.sub( input2 );
			return output;
		}

		@Override
		public AddOpOld< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static abstract class AddOp< T extends NumericType< T > > implements Runnable
	{
		protected T input1;

		protected T input2;

		protected T output;

		private AddOp()
		{
			input1 = null;
			input2 = null;
		}

		public void setInput1( final T t )
		{
			input1 = t;
		}

		public void setInput2( final T t )
		{
			input2 = t;
		}

		public void setOutput( final T t )
		{
			output = t;
		}

		public AddOp< T > recreate( final Runnable op1, final Runnable op2 )
		{
			return create( op1, op2 );
		}

		private static < T extends NumericType< T > > AddOp< T > create( final Runnable op1, final Runnable op2 )
		{
			if ( op1 instanceof EmptyOp )
				if ( op2 instanceof EmptyOp )
					return new AddOp< T >()
					{
						@Override
						public void run()
						{
							output.set( input1 );
							output.add( input2 );
						}
					};
				else
					return new AddOp< T >()
					{
						@Override
						public void run()
						{
							op2.run();
							output.set( input1 );
							output.add( input2 );
						}
					};
			else if ( op2 instanceof EmptyOp )
				return new AddOp< T >()
				{
					@Override
					public void run()
					{
						op1.run();
						output.set( input1 );
						output.add( input2 );
					}
				};
			else
				return new AddOp< T >()
				{
					@Override
					public void run()
					{
						op1.run();
						op2.run();
						output.set( input1 );
						output.add( input2 );
					}
				};
		}
	}

	public static class EmptyOp implements Runnable
	{
		@Override
		public void run()
		{
		}
	}

	public static class SumExpression< T extends NumericType< T > > implements Sampler< T >
	{
		protected Sampler< T > a = null;

		protected Sampler< T > b = null;

		protected Sampler< T > c = null;

		protected void setA( final Sampler< T > s )
		{
			a = s;
		}

		protected void setB( final Sampler< T > s )
		{
			b = s;
		}

		protected void setC( final Sampler< T > s )
		{
			c = s;
		}

		@Override
		public T get()
		{
			final T t = c.get();
			t.set( a.get() );
			t.add( b.get() );
			return t;
		}

		@Override
		public SumExpression< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final class Holds< T > implements Sampler< T >
	{
		private final T t;

		public Holds( final T t )
		{
			this.t = t;
		}

		@Override
		public T get()
		{
			return t;
		}

		@Override
		public Sampler< T > copy()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static void main( final String args[] )
	{
		final Img< FloatType > imgA = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgB = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgC = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgD = ArrayImgs.floats( 5000, 5000 );
		final Img< FloatType > imgE = ArrayImgs.floats( 5000, 5000 );

		int i = 0;
		for ( final FloatType t : imgA )
			t.set( i++ );
		i = 0;
		for ( final FloatType t : imgB )
			t.set( i++ );
		i = 0;
		for ( final FloatType t : imgC )
			t.set( i++ );
		i = 0;
		for ( final FloatType t : imgD )
			t.set( i++ );

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< FloatType > ca = imgA.cursor();
				final Cursor< FloatType > cb = imgB.cursor();
				final Cursor< FloatType > cc = imgC.cursor();
				final Cursor< FloatType > cd = imgD.cursor();
				final Cursor< FloatType > ce = imgE.cursor();

				final SumExpression< FloatType > e1 = new SumExpression< FloatType >();
				e1.setA( ca );
				e1.setB( cb );
				e1.setC( new Holds< FloatType >( new FloatType() ) );
				final SumExpression< FloatType > e2 = new SumExpression< FloatType >();
				e2.setA( cc );
				e2.setB( cd );
				e2.setC( new Holds< FloatType >( new FloatType() ) );
				final SumExpression< FloatType > e3 = new SumExpression< FloatType >();
				e3.setA( e1 );
				e3.setB( e2 );
				e3.setC( ce );

				while ( cd.hasNext() )
				{
					ca.fwd();
					cb.fwd();
					cc.fwd();
					cd.fwd();
					ce.fwd();
					e3.get();
				}
			}
		} );

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< FloatType > ca = imgA.cursor();
				final Cursor< FloatType > cb = imgB.cursor();
				final Cursor< FloatType > cc = imgC.cursor();
				final Cursor< FloatType > cd = imgD.cursor();
				final AddOp< FloatType > op1 = AddOp.<FloatType>create( new EmptyOp(), new EmptyOp() );
				final AddOp< FloatType > op2 = AddOp.<FloatType>create( new EmptyOp(), new EmptyOp() );
				final AddOp< FloatType > op3 = AddOp.<FloatType>create( op1, op2 );
				final FloatType tmp1 = new FloatType();
				final FloatType tmp2 = new FloatType();
				op1.setOutput( tmp1 );
				op2.setOutput( tmp2 );
				op3.setInput1( tmp1 );
				op3.setInput2( tmp2 );
				for ( final FloatType t : imgE )
				{
					op1.setInput1( ca.next() );
					op1.setInput2( cb.next() );
					op2.setInput1( cc.next() );
					op2.setInput2( cd.next() );
					op3.setOutput( t );
					op3.run();
				}
			}
		} );

		BenchmarkHelper.benchmarkAndPrint( 10, true, new Runnable()
		{
			@Override
			public void run()
			{
				final Cursor< FloatType > ca = imgA.cursor();
				final Cursor< FloatType > cb = imgB.cursor();
				final Cursor< FloatType > cc = imgC.cursor();
				final Cursor< FloatType > cd = imgD.cursor();
				final BinaryOperation< FloatType, FloatType, FloatType > op1 = new AddOpOld< FloatType >();
				final BinaryOperation< FloatType, FloatType, FloatType > op2 = new AddOpOld< FloatType >();
				final BinaryOperation< FloatType, FloatType, FloatType > op3 = new SubOpOld< FloatType >();
				final FloatType tmp1 = new FloatType();
				final FloatType tmp2 = new FloatType();
				for ( final FloatType t : imgE )
				{
					compute( op1, ca.next(), cb.next(), tmp1 );
					compute( op2, tmp1, cc.next(), tmp2 );
					compute( op3, tmp2, cd.next(), t );
				}
			}
		} );

		final Cursor< FloatType > ce = imgE.cursor();
		for ( i = 0; i < 10; ++i )
			System.out.print( ce.next().get() + "  " );
		System.out.println();
	}

	static <A,B,C> void compute( final BinaryOperation< A,B,C > op, final A a, final B b, final C c )
	{
		op.compute(a,b,c);
	}
}