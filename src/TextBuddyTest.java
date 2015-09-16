

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TextBuddyTest {
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
    
    @Test
    public void fileReadTest(){
        //create test file to read
        String[] dataLines = { "First line", "Second line",
                                "Third line" };
        
        Random rng = new Random();
        String fileName ="test" + rng.nextInt() + ".txt";
        File file;
        try {
            file = new File(fileName);
            while(file.exists()){
                fileName ="test" + rng.nextInt() + ".txt";
                file = new File(fileName);
            }
            file.createNewFile();
        
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for (String line : dataLines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        assertArrayEquals(dataLines, textBuddy.getDataFromFile().toArray());
   
        //cleaning the test file
        try {
            file = new File(fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
