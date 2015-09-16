

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TextBuddyTest {
    TextBuddy textBuddy = new TextBuddy("file.txt");
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }
    
    @Test
    public void commandObjectNoParameterTest(){
        String message = "Clear";
        TextBuddy.CommandObject cmdObj = new TextBuddy.CommandObject(message);
        assertEquals(message, cmdObj.getCommand());
        assertFalse(cmdObj.hasParameters());
        assertEquals(null, cmdObj.getParameters());
    }
    
    @Test
    public void commandObjectWithStringParameter(){
        String message = "Add ten pies";
        TextBuddy.CommandObject cmdObj = new TextBuddy.CommandObject(message);
        assertEquals("Add", cmdObj.getCommand());
        assertTrue(cmdObj.hasParameters());
        assertEquals("ten pies", cmdObj.getParameters());
        assertFalse(cmdObj.processParameterAsInteger());
    }
    
    @Test
    public void commandObjectWithIntegerParameter(){
        String message = "Delete 1000";
        TextBuddy.CommandObject cmdObj = new TextBuddy.CommandObject(message);
        assertEquals("Delete", cmdObj.getCommand());
        assertTrue(cmdObj.hasParameters());
        assertEquals("1000", cmdObj.getParameters());
        assertTrue(cmdObj.processParameterAsInteger());
        assertEquals(1000, cmdObj.getParameterAsInteger());
    }
    
    @Test
    public void printMessageTest(){
        String message = "hello";
        TextBuddy.printMessage(message);
        assertEquals(message + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void arguementNotOneTest(){
        String[] args = {"0", "1" };
        assertFalse(TextBuddy.isOfOneArgument(args));
    }

}
