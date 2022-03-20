package net.imglib2.img.basictypeaccess.arrayNd;

import net.imglib2.Localizable;
import net.imglib2.type.numeric.real.DoubleType;

public class DoubleArray3DImg extends AbstractArrayNDImg<DoubleType> {

    protected double[][][] data;

    protected DoubleType value;

    public DoubleArray3DImg( final double[][][] data )
    {
        super( new long[]{ data.length, data[0].length, data[0][0].length } );
        this.data = data;
        value = new DoubleType();
    }

    @Override
    public double[][][] getData()
    {
       return data;
    }

    @Override
    public DoubleType getType() {
        return value;
    }

    public double get( int x, int y, int z )
    {
        return data[x][y][z];
    }

    public double get( int[] pos )
    {
        return get( pos[0], pos[1], pos[2] );
    }

    @Override
    public void get(int[] pos, DoubleType dest) {
         dest.set( get( pos ));
    }

    @Override
    public void set(int[] pos, DoubleType src) {
        data[pos[0]][pos[1]][pos[2]]= src.getRealDouble();
    }

    @Override
    public void get(Localizable pos, DoubleType dest) {
        dest.set( data[ pos.getIntPosition(0)][ pos.getIntPosition(1)][pos.getIntPosition(2)]);
    }

    @Override
    public void set(Localizable pos, DoubleType src) {
        data[ pos.getIntPosition(0)][ pos.getIntPosition(1)][pos.getIntPosition(2)] = src.getRealDouble();
    }

}
