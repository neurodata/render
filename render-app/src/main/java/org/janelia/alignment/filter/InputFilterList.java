package org.janelia.alignment.filter;

import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on {@link mpicbg.models.CoordinateTransformList}.
 */
public class InputFilterList< E extends InputFilter > implements InputFilter {

    final protected List< E > filters = new ArrayList< E >();
    final public void add( final E t ){ filters.add( t ); }
    final public void remove( final E t ){ filters.remove( t ); }
    final public E remove( final int i ){ return filters.remove( i ); }
    final public E get( final int i ){ return filters.get( i ); }
    final public void clear(){ filters.clear(); }
    final public List< E > getList( final List< E > preAllocatedList )
    {
        final List< E > returnList = ( preAllocatedList == null ) ? new ArrayList< E >() : preAllocatedList;
        returnList.addAll(filters);
        return returnList;
    }


    /* Should the filter list behave like a filter? Maybe this is wrong. */
    // Apply filters in order.
    public ImageProcessor process(ImageProcessor ip, final double scale) {
        for (InputFilter filter : filters) {
            ip = filter.process(ip, scale);
        }
        return ip;
    }

    public void init(String string) { }
    public String toDataString() { return null; }


}