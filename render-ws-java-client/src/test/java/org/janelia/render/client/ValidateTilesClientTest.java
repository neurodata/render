package org.janelia.render.client;

import org.janelia.render.client.parameter.CommandLineParameters;
import org.junit.Test;

/**
 * Tests the {@link ValidateTilesClient} class.
 *
 * @author Eric Trautman
 */
public class ValidateTilesClientTest {

    @Test
    public void testParameterParsing() throws Exception {
        CommandLineParameters.parseHelp(new ValidateTilesClient.Parameters());
    }

}
