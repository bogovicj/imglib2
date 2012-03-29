/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package tests;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import mpicbg.util.Timer;
import net.imglib2.RandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class OpenAndDisplayAffineTransformedScreenImage
{	
	final static public void main( final String[] args )
		throws ImgIOException
	{
		new ImageJ();
		
		final ImgOpener io = new ImgOpener();
		//final RandomAccessibleInterval< UnsignedShortType > img = io.openImg( "/home/saalfeld/phd/light-microscopy/presentation/saalfeld-05-05-4-DPX-05_L1_Sum.lsm", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType());
		final ImgPlus< UnsignedShortType > imgPlus = io.openImg( "/home/saalfeld/phd/light-microscopy/presentation/saalfeld-05-05-4-DPX-05_L1_Sum.0.tif", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType());
		final Img< UnsignedShortType > img = imgPlus.getImg();
		
		final double[][] matrix = new double[][]{
				{ 0.5, 0, 0, img.dimension( 0 ) * 0.25 },
				{ 0, 0.5 * imgPlus.calibration( 1 ) / imgPlus.calibration( 0 ), 0, img.dimension( 1 ) * 0.25 },
				{ 0, 0, 0.5 * imgPlus.calibration( 0 ) / imgPlus.calibration( 0 ), 0 }
		};
//		final AffineTransform affine = new AffineTransform( new Matrix( matrix ) );
		final AffineTransform3D affine = new AffineTransform3D();
		affine.set( matrix[ 0 ][ 0 ], matrix[ 0 ][ 1 ], matrix[ 0 ][ 2 ], matrix[ 0 ][ 3 ], matrix[ 1 ][ 0 ], matrix[ 1 ][ 1 ], matrix[ 1 ][ 2 ], matrix[ 1 ][ 3 ], matrix[ 2 ][ 0 ], matrix[ 2 ][ 1 ], matrix[ 2 ][ 2 ], matrix[ 2 ][ 3 ] );
		
		final NearestNeighborInterpolatorFactory< UnsignedShortType > interpolatorFactory = new NearestNeighborInterpolatorFactory< UnsignedShortType >();
//		final InterpolatorFactory< UnsignedShortType, RandomAccessible< UnsignedShortType> > interpolatorFactory = new NLinearInterpolatorFactory< UnsignedShortType >();
		
		final RandomAccessible< UnsignedShortType > extendedImg = Views.extendValue( img, new UnsignedShortType() );
		//final RandomAccessible< UnsignedShortType > channel = Views.hyperSlice( img, 2, 0 );
		//final Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolant = new Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > >( channel, interpolatorFactory );
		final Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolant = new Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > >( extendedImg, interpolatorFactory );
//		final RealTransformRandomAccessible< UnsignedShortType, AffineTransform3D > mapping = new RealTransformRandomAccessible< UnsignedShortType, AffineTransform3D >( interpolant, affine );
//		final AffineRealRandomAccessible< UnsignedShortType, Affine > mapping = new AffineRealRandomAccessible< UnsignedShortType, Affine >( interpolant, affine );
		final AffineRandomAccessible< UnsignedShortType, AffineGet > mapping = new AffineRandomAccessible< UnsignedShortType, AffineGet >( interpolant, affine );
//		final ConstantAffineRandomAccessible< UnsignedShortType, AffineTransform3D > mapping = new ConstantAffineRandomAccessible< UnsignedShortType, AffineTransform3D >( interpolant, affine );
//		final RandomAccessibleOnRealRandomAccessible< UnsignedShortType > transformedPixels = new RandomAccessibleOnRealRandomAccessible< UnsignedShortType >( mapping );
		
		final ColorProcessor cp = new ColorProcessor( ( int )img.dimension( 0 ), ( int )img.dimension( 1 ) );
		final ARGBScreenImage screenImage = new ARGBScreenImage( cp.getWidth(), cp.getHeight(), ( int[] )cp.getPixels() );
//		final XYProjector< UnsignedShortType, ARGBType > projector = new XYProjector< UnsignedShortType, ARGBType >( mapping, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
//		final XYProjector< UnsignedShortType, ARGBType > projector = new XYProjector< UnsignedShortType, ARGBType >( transformedPixels, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
		final XYRandomAccessibleProjector< UnsignedShortType, ARGBType > projector = new XYRandomAccessibleProjector< UnsignedShortType, ARGBType >( mapping, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
//		final XYRandomAccessibleProjector< UnsignedShortType, ARGBType > projector = new XYRandomAccessibleProjector< UnsignedShortType, ARGBType >( transformedPixels, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
		
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		
		final Timer timer = new Timer();
		
		for ( int k = 0; k < 0; ++k )
		{
			timer.start();
//			final long last = img.dimension( 3 ) * 2 - 2;
			final long last = img.dimension( 2 ) * 2 - 2;
			for ( int i = 0; i < last; ++i )
			{
				projector.setPosition( i, 2 );
				projector.map();
				imp.updateAndDraw();
			}
			IJ.log( "loop " + ( k + 1 ) + ": " + timer.stop() );
		}
		
		projector.setPosition( img.dimension( 2 ) / 2, 2 );
		
		final AffineTransform3D forward = new AffineTransform3D();
		final AffineTransform3D rotation = new AffineTransform3D();
		final AffineTransform3D scale = new AffineTransform3D();
		scale.set(
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0 );
		
		for ( int k = 0; k < 3; ++k )
		{	
			timer.start();
			for ( int i = 0; i < 360; ++i )
			{
				rotation.rotate( 1, Math.PI / 360 );
				forward.set(
						1.0, 0, 0, -img.dimension( 0 ) / 2.0,
						0, 1.0, 0, -img.dimension( 1 ) / 2.0,
						0, 0, 1.0, -img.dimension( 2 ) / 2.0 );
				forward.preConcatenate( scale );
				forward.preConcatenate( rotation );
				forward.set(
						forward.get( 0, 0 ), forward.get( 0, 1 ), forward.get( 0, 2 ), forward.get( 0, 3 ) + img.dimension( 0 ) / 2.0,
						forward.get( 1, 0 ), forward.get( 1, 1 ), forward.get( 1, 2 ), forward.get( 1, 3 ) + img.dimension( 1 ) / 2.0,
						forward.get( 2, 0 ), forward.get( 2, 1 ), forward.get( 2, 2 ), forward.get( 2, 3 ) + img.dimension( 2 ) / 2.0 );
				
				affine.set( forward.inverse() );
				
//				final ConstantAffineRandomAccessible< UnsignedShortType, AffineTransform3D > mapping1 = new ConstantAffineRandomAccessible< UnsignedShortType, AffineTransform3D >( interpolant, affine );
//				final XYRandomAccessibleProjector< UnsignedShortType, ARGBType > projector1 = new XYRandomAccessibleProjector< UnsignedShortType, ARGBType >( mapping1, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
//				projector1.setPosition( img.dimension( 2 ) / 2, 2 );
//				
				//System.out.println( affine );
				
				projector.map();
				imp.updateAndDraw();
			}
			IJ.log( "loop " + ( k + 1 ) + ": " + timer.stop() );
		}
		
		final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
		imp.setProcessor( cpa );
		imp.updateAndDraw();
	}
}