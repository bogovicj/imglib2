package script.imglib.test;


import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.script.algorithm.integral.histogram.IntegralHistogramCursor;
import net.imglib2.script.algorithm.integral.histogram.features.IHMax;
import net.imglib2.script.algorithm.integral.histogram.features.IHMean;
import net.imglib2.script.algorithm.integral.histogram.features.IHMedian;
import net.imglib2.script.algorithm.integral.histogram.features.IHMin;
import net.imglib2.script.algorithm.integral.histogram.features.IHStdDev;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class HistogramFeatures<T extends RealType<T> & NativeType<T>, P extends IntegerType<P> & NativeType<P>> extends ImgProxy<T>
{
	static public final int NUM_FEATURES = 5;
	
	public HistogramFeatures(
			final Img<T> img,
			final Img<P> integralHistogram,
			final Histogram histogram,
			final long[] radius) {
		super(create(img, integralHistogram, histogram, radius));
	}

	private static final <R extends RealType<R> & NativeType<R>, P extends IntegerType<P> & NativeType<P>>
	Img<R> create(
			final Img<R> img,
			final Img<P> integralHistogram,
			final Histogram histogram,
			final long[] radius)
	{	
		final long[] dims = new long[img.numDimensions() + 1];
		for (int d=0; d<dims.length -1; ++d) dims[d] = img.dimension(d);
		dims[dims.length -1] = NUM_FEATURES;
		
		final Img<R> features = img.factory().create(dims, img.firstElement().createVariable());
		final RandomAccess<R> fr = features.randomAccess();
		
		// One histogram per pixel position, representing the histogram of the window centered at that pixel
		final IntegralHistogramCursor<P> h = new IntegralHistogramCursor<P>(integralHistogram, histogram, radius);
		
		final int lastDimension = fr.numDimensions() -1;
		
		final IHMin<DoubleType> ihMin = new IHMin<DoubleType>();
		final IHMax<DoubleType> ihMax = new IHMax<DoubleType>();
		final IHMean<DoubleType> ihMean = new IHMean<DoubleType>();
		final IHMedian<DoubleType> ihMedian = new IHMedian<DoubleType>();
		final IHStdDev<DoubleType> ihStdDev = new IHStdDev<DoubleType>();
		
		while (h.hasNext()) {
			h.fwd();
			for (int d=0; d<h.numDimensions(); ++d) {
				fr.setPosition(h.getLongPosition(d), d);
			}
			// Compute features: gets put into the histogram, which is reused,
			// but the local pointer helps performance
			final Histogram hist = h.get();
			
			double imgMin = ihMin.get(hist);
			double imgMax = ihMax.get(hist);
			double imgMean = ihMean.get(hist);
			double imgMedian = ihMedian.get(hist);
			double imgStdDev = ihStdDev.get(hist, imgMedian);

			// TODO above, the features should be composable, so that some features depend on others
			// like stdDev depends on the median (or the mean).
			// But in the end the results must return in order, with perhaps some results being a vector of multiple results.

			
			// Store
			fr.setPosition(0, lastDimension);
			fr.get().setReal(imgMin);
			fr.move(1, lastDimension);
			fr.get().setReal(imgMax);
			fr.move(1, lastDimension);
			fr.get().setReal(imgMean);
			fr.move(1, lastDimension);
			fr.get().setReal(imgMedian);
			fr.move(1, lastDimension);
			fr.get().setReal(imgStdDev);
		}

		return features;
	}
}