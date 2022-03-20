package net.imglib2.img.basictypeaccess.arrayNd;

import net.imglib2.*;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.type.NativeType;

public class ArrayNdCursor< T extends NativeType<T>, A extends AbstractArrayNDImg<T>> extends IntervalIterator implements Cursor< T > {

    protected final A img;

    protected final T value;

    public ArrayNdCursor(final ArrayNdCursor<T,A> cursor )
    {
        super( cursor );
        this.img = cursor.img;
        value = cursor.value.copy();
    }

    public ArrayNdCursor(final A img )
    {
        super( img );
        this.img = img;
        value = img.getType();
    }

    @Override
    public T get() {
        img.get( this, value );
        return value;
    }

    @Override
    public ArrayNdCursor< T, A > copy() {
        return new ArrayNdCursor<T,A>( this );
    }

    @Override
    public ArrayNdCursor< T, A > copyCursor()
    {
        return copy();
    }

    @Override
    public T next() {
        fwd();
        return get();
    }
}
