package com.developerphil.adbidea.adb.command;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by pbreault on 1/5/14.
 */
public class StartActivityReceiverTest {


    public static final String[] TRAILING_EMPTY_LINE = new String[]{""};

    @Test
    public void testReceiverSuccess() throws Exception {
        String[] lines = new String[]{
                "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.example.untitled/.MyActivity }"
        };

        StartDefaultActivityCommand.StartActivityReceiver receiver = new StartDefaultActivityCommand.StartActivityReceiver();
        assertThat(receiver.isSuccess(), is(false));

        receiver.processNewLines(lines);
        receiver.processNewLines(TRAILING_EMPTY_LINE);
        assertThat(receiver.isSuccess(), is(true));
    }

    @Test
    public void isSuccessWhenAppIsAlreadyStarted() throws Exception {
        String[] lines = new String[]{
                "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.example.untitled/.MyActivity }",
                "Warning: Activity not started, its current task has been brought to the front"
        };

        StartDefaultActivityCommand.StartActivityReceiver receiver = new StartDefaultActivityCommand.StartActivityReceiver();
        receiver.processNewLines(lines);
        receiver.processNewLines(TRAILING_EMPTY_LINE);

        assertThat(receiver.isSuccess(), is(true));
    }


    @Test
    public void isFailureWhenAppIsUninstalled() throws Exception {
        String[] lines = new String[]{
                "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.cxategory.LAUNCHER] cmp=com.example.untitled/.MyActivity }",
                "Error type 3",
                "Error: Activity class {com.example.untitled/com.example.untitled.MyActivity} does not exist."
        };

        StartDefaultActivityCommand.StartActivityReceiver receiver = new StartDefaultActivityCommand.StartActivityReceiver();
        receiver.processNewLines(lines);
        receiver.processNewLines(TRAILING_EMPTY_LINE);

        assertThat(receiver.isSuccess(), is(false));

        assertThat(receiver.getMessage(), is(equalTo(
                "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.cxategory.LAUNCHER] cmp=com.example.untitled/.MyActivity }\n" +
                        "Error type 3\n" +
                        "Error: Activity class {com.example.untitled/com.example.untitled.MyActivity} does not exist."
        )));

    }
}
