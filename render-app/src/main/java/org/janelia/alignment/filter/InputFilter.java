package org.janelia.alignment.filter;

import ij.process.ImageProcessor;

import java.io.Serializable;

/**
 * Interface for filters intended to be used on input data, before any transformations are applied.
 *
 * These are currently applied as part of the mipmap hierarchy.
 * An expected use would be to apply to level 0 (raw data) only, and other mipmap levels would
 * be materialized based on its output.  (This may or may not be reasonable.  Open to suggestions.)
 *
 * @author Eric Perlman
 */
public interface InputFilter extends Serializable {
    public ImageProcessor process(ImageProcessor ip, final double scale);

    public void init(String string);
    public String toDataString();
}
