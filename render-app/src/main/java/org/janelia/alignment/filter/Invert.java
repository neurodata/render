package org.janelia.alignment.filter;

import ij.process.ImageProcessor;

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

    public void init(String string) { };
    public String toDataString() {
        return "";
    }

}
