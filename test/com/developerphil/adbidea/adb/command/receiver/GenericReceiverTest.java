package com.developerphil.adbidea.adb.command.receiver;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class GenericReceiverTest {

    @Test
    public void testReceiverRecordsAdbOutput() throws Exception {
        GenericReceiver receiver = new GenericReceiver();
        assertTrue(receiver.getAdbOutputLines().isEmpty());

        receiver.processNewLines(new String[]{"1", "2", "3"});
        assertThat(receiver.getAdbOutputLines(), CoreMatchers.hasItems("1", "2", "3"));

        receiver.processNewLines(new String[]{"4"});
        assertThat(receiver.getAdbOutputLines(), CoreMatchers.hasItems("1", "2", "3", "4"));
    }

}
