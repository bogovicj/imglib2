package net.imglib2.img.basictypeaccess.arrayNd;

import net.imglib2.Localizable;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.IntervalIndexer;

import java.util.Arrays;

public class DoubleArray3DAccess implements DoubleAccess {

    protected double[][][] data;

    protected long[] dimensions;

    protected int[] position;

    public DoubleArray3DAccess(final double[][][] data )
    {
        this.data = data;
        position = new int[ 3 ];
        dimensions = new long[]{ data[0][0].length, data[0].length, data.length };
    }

    public double[][][] getData()
    {
       return data;
    }

    public double get( int x, int y, int z )
    {
        return data[z][y][x];
    }

    public double get( int[] pos )
    {
        return data[pos[2]][pos[1]][pos[0]];
    }

    public void get(int[] pos, DoubleType dest) {
         dest.set( get( pos ));
    }

    public void set(int[] pos, DoubleType src) {
        data[pos[2]][pos[1]][pos[0]]= src.getRealDouble();
    }

    public void set(int[] pos, double src) {
        data[pos[2]][pos[1]][pos[0]]= src;
    }

    public void get(Localizable pos, DoubleType dest) {
        dest.set( data[ pos.getIntPosition(2)][ pos.getIntPosition(1)][pos.getIntPosition(0)]);
    }

    public void set(Localizable pos, DoubleType src) {
        data[ pos.getIntPosition(2)][ pos.getIntPosition(1)][pos.getIntPosition(0)] = src.getRealDouble();
    }

    public void set(Localizable pos, double src) {
        data[ pos.getIntPosition(2)][ pos.getIntPosition(1)][pos.getIntPosition(0)] = src;
    }

    @Override
    public double getValue(int index) {
        IntervalIndexer.indexToPosition( index, dimensions, position );
        return get( position );
    }

    @Override
    public void setValue(int index, double value) {
        IntervalIndexer.indexToPosition( index, dimensions, position );
        set( position, value );
    }
}
