package org.janelia.alignment.filter;

import ij.process.ImageProcessor;

import java.util.Collections;
import java.util.Map;

/**
 * A very simple filter class to invert the contents of an image.
 *
 * @author Eric Perlman
 */
public class Invert implements Filter, InputFilter {
    @Override
    public ImageProcessor process(ImageProcessor ip, final double scale) {
        ip.invert();
        return ip;
    }

    /* Invert does not take or return any parameters */
    public void init(Map<String, String> params) {
    }

    public Map<String, String> getParams() {
        return Collections.emptyMap();
    }
}
