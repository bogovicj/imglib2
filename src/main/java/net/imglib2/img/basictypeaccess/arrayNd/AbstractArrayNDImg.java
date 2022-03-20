package net.imglib2.img.basictypeaccess.arrayNd;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;

public abstract class AbstractArrayNDImg<T extends NativeType<T>> extends AbstractImg<T> {

    public abstract Object getData();

    public abstract T getType();

    public AbstractArrayNDImg(long[] size) {
        super(size);
    }

    /**
     * Gets the value of this image at the given position, modifying the value of dest
     *
     * @param pos position
     * @param dest object to be modiefied
     */
    public abstract void get( int[] pos, T dest );

    /**
     * Sets the value of this image at the given position to the value of src.
     *
     * @param pos position
     * @param src object to be modiefied
     */
    public abstract void set( int[] pos, T src );

    /**
     * Gets the value of this image at the given position, modifying the value of dest
     *
     * @param pos position
     * @param dest object to be modiefied
     */
    public abstract void get( final Localizable pos, T dest );

    /**
     * Sets the value of this image at the given position to the value of src.
     *
     * @param pos position
     * @param src object to be modiefied
     */
    public abstract void set( final Localizable pos, T src );

    @Override
    public Cursor<T> cursor() {
        return new ArrayNdCursor( this );
    }

    @Override
    public Cursor<T> localizingCursor() {
        return cursor();
    }

    @Override
    public Object iterationOrder() {
        return null;
    }

    @Override
    public RandomAccess<T> randomAccess() {
        return new ArrayNdRandomAccess(this);
    }

    @Override
    public ImgFactory<T> factory() {
        return null;
    }

    @Override
    public Img<T> copy() {
        return null;
    }
}
