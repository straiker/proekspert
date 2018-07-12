package org.rait.proekspert.main;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MainTest {
    private final double listSize = 100;
    private Main main = new Main();


    @Test
    public void testGetPercentage() throws Exception {
        assertTrue(main.getPercentage(listSize,10) == 10);
    }

    @Test
    public void testSetGetListSize() throws Exception {
        main.renderFileData();
        assertTrue(!main.getList().isEmpty());
    }

}
