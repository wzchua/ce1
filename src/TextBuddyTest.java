

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

    public String initializeDummyFile(String[] dataLines) {
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
        return fileName;
    }
    
    public void deleteDummyFile(String fileName){
        File file;
        //cleaning the test file
        try {
            file = new File(fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
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
        
       String fileName = initializeDummyFile(dataLines);
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        assertArrayEquals(dataLines, textBuddy.getDataFromFile().toArray());
   
       deleteDummyFile(fileName);
    }
    
    @Test
    public void displayEntriesTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        assertEquals("1. test" +System.lineSeparator() + "2. test2", textBuddy.displayEntries());
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void processDisplayCommandTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        TextBuddy.CommandObject testCommmand1 = new TextBuddy.CommandObject("Display");
        TextBuddy.CommandObject invalidClearCommand  = new TextBuddy.CommandObject("Display 5");
        String invalidCommandOutput = "Invalid command parameter";
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        
        assertEquals("1. test" +System.lineSeparator() + "2. test2", textBuddy.processDisplayCommand(testCommmand1));
        assertEquals(invalidCommandOutput, textBuddy.processDisplayCommand(invalidClearCommand));
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void clearEntriesTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        String clearOutput = String.format("all content deleted from %1$s", fileName);
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        assertEquals(clearOutput, textBuddy.clearEntries());
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void processClearCommandTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        String invalidCommandOutput = "Invalid command parameter";
        TextBuddy.CommandObject validClearCommmand = new TextBuddy.CommandObject("Clear");
        TextBuddy.CommandObject invalidClearCommand = new TextBuddy.CommandObject("Clear 3");
        String clearOutput = String.format("all content deleted from %1$s", fileName);
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        assertEquals(clearOutput, textBuddy.processClearCommand(validClearCommmand));
        assertEquals(invalidCommandOutput, textBuddy.processClearCommand(invalidClearCommand));
        
        deleteDummyFile(fileName);
    }
}
