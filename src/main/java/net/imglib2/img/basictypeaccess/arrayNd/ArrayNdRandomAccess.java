package net.imglib2.img.basictypeaccess.arrayNd;

import net.imglib2.AbstractLocalizableInt;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.type.NativeType;

public class ArrayNdRandomAccess< T extends NativeType<T>, A extends AbstractArrayNDImg<T>> extends AbstractLocalizableInt implements RandomAccess< T > {

    private final A img;

    private final T value;

    public ArrayNdRandomAccess(final ArrayNdRandomAccess<T,A> randomAccess )
    {
        super( randomAccess.numDimensions() );
        this.img = randomAccess.img;
        setPosition( randomAccess );
        value = randomAccess.value.copy();
    }

    public ArrayNdRandomAccess(final A img )
    {
        super( img.numDimensions() );
        this.img = img;
        value = img.getType().copy();
    }

    @Override
    public ArrayNdRandomAccess copyRandomAccess() {
        return new ArrayNdRandomAccess(this);
    }

    @Override
    public T get()
    {
        img.get( this, value );
        return value;
    }

    @Override
    public Sampler<T> copy() {
        return null;
    }

    public void fwd( final int d )
    {
        ++position[ d ];
    }

    @Override
    public void bck( final int d )
    {
        --position[ d ];
    }

    @Override
    public void move( final int distance, final int d )
    {
        position[ d ] += distance;
    }

    @Override
    public void move( final long distance, final int d )
    {
        position[ d ] += distance;
    }

    @Override
    public void move( final Localizable localizable )
    {
        int move = 0;
        for ( int d = 0; d < n; ++d )
        {
            final int distance = localizable.getIntPosition( d );
            position[ d ] += distance;
        }
    }

    @Override
    public void move( final int[] distance )
    {
        int move = 0;
        for ( int d = 0; d < n; ++d )
        {
            position[ d ] += distance[ d ];
        }
    }

    @Override
    public void move( final long[] distance )
    {
        int move = 0;
        for ( int d = 0; d < n; ++d )
        {
            position[ d ] += distance[ d ];
        }
    }

    @Override
    public void setPosition( final Localizable localizable )
    {
        int i = 0;
        for ( int d = 0; d < n; ++d )
        {
            position[ d ] = localizable.getIntPosition( d );
        }
    }

    @Override
    public void setPosition( final int[] pos )
    {
        int i = 0;
        for ( int d = 0; d < n; ++d )
        {
            position[ d ] = pos[ d ];
        }
    }

    @Override
    public void setPosition( final long[] pos )
    {
        int i = 0;
        for ( int d = 0; d < n; ++d )
        {
            final int p = ( int ) pos[ d ];
            position[ d ] = p;
        }
    }

    @Override
    public void setPosition( final int pos, final int d )
    {
        position[ d ] = pos;
    }

    @Override
    public void setPosition( final long pos, final int d )
    {
        position[ d ] = ( int ) pos;
    }

}
