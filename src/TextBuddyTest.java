

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
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
    
    @Test
    public void displayEntriesTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        assertEquals("1. test" +System.lineSeparator() + "2. test2", textBuddy.displayEntries());
        textBuddy.clearEntries();
        assertEquals(fileName + " is empty", textBuddy.displayEntries());
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void processDisplayCommandTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        TextBuddy.CommandObject validDisplayCommand = new TextBuddy.CommandObject("Display");
        TextBuddy.CommandObject invalidDisplayCommand  = new TextBuddy.CommandObject("Display 5");
        String invalidCommandOutput = "Invalid command parameter";
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        
        assertEquals("1. test" +System.lineSeparator() + "2. test2", textBuddy.processDisplayCommand(validDisplayCommand));
        assertEquals(invalidCommandOutput, textBuddy.processDisplayCommand(invalidDisplayCommand));
        textBuddy.clearEntries();
        assertEquals(fileName + " is empty", textBuddy.processDisplayCommand(validDisplayCommand));
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void deleteEntryTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        String deleteOutput = String.format("deleted from %1$s: \"%2$s\"", fileName, "test2");
        String[] dataAfterDelete = {"test"};
        String invalidIndexOutput = "Invalid index";
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        assertArrayEquals(testData, textBuddy.getDataLines().toArray());
        assertEquals(deleteOutput, textBuddy.deleteEntry(1));
        assertArrayEquals(dataAfterDelete, textBuddy.getDataLines().toArray());
        
        assertEquals(invalidIndexOutput, textBuddy.deleteEntry(10));
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void processDeleteCommandTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        String deleteOutput = String.format("deleted from %1$s: \"%2$s\"", fileName, "test2");
        TextBuddy.CommandObject validDeleteCommand = new TextBuddy.CommandObject("Delete 2");
        TextBuddy.CommandObject invalidDeleteCommand1  = new TextBuddy.CommandObject("Delete");
        TextBuddy.CommandObject invalidDeleteCommand2  = new TextBuddy.CommandObject("Delete 10");
        String invalidCommandOutput = "Invalid command parameter";
        String invalidIndexOutput = "Invalid index";
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        String[] dataAfterDelete = {"test"};
        
        assertArrayEquals(testData, textBuddy.getDataLines().toArray());
        assertEquals(deleteOutput, textBuddy.processDeleteCommand(validDeleteCommand));
        assertArrayEquals(dataAfterDelete, textBuddy.getDataLines().toArray());
        
        assertEquals(invalidCommandOutput, textBuddy.processDeleteCommand(invalidDeleteCommand1));
        assertEquals(invalidIndexOutput, textBuddy.processDeleteCommand(invalidDeleteCommand2));
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void addEntryTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        String addedLine = "test3";
        String addOutput = String.format("added to %1$s: \"%2$s\"", fileName, addedLine);
        String[] dataAfterAdd = {"test", "test2", addedLine};
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        assertArrayEquals(testData, textBuddy.getDataLines().toArray());
        assertEquals(addOutput, textBuddy.addEntry(addedLine));
        assertArrayEquals(dataAfterAdd, textBuddy.getDataLines().toArray());
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void processAddCommandTest(){
        String[] testData = {"test", "test2"};
        String fileName = initializeDummyFile(testData);
        String addedLine = "test3";
        String addOutput = String.format("added to %1$s: \"%2$s\"", fileName, addedLine);
        TextBuddy.CommandObject validDeleteCommand = new TextBuddy.CommandObject("Add " + addedLine);
        TextBuddy.CommandObject invalidDeleteCommand  = new TextBuddy.CommandObject("Add");
        String invalidCommandOutput = "Invalid command parameter";
        
        TextBuddy textBuddy = new TextBuddy(fileName);
        String[] dataAfterAdd = {"test", "test2", addedLine};
        
        assertArrayEquals(testData, textBuddy.getDataLines().toArray());
        assertEquals(addOutput, textBuddy.processAddCommand(validDeleteCommand));
        assertArrayEquals(dataAfterAdd, textBuddy.getDataLines().toArray());
        
        assertEquals(invalidCommandOutput, textBuddy.processDeleteCommand(invalidDeleteCommand));
        
        deleteDummyFile(fileName);
    }
    
    @Test
    public void sortEntriesTest(){
        String[] testData = new String[0];
        String fileName = initializeDummyFile(testData);
        TextBuddy textBuddy = new TextBuddy(fileName);
        ArrayList<String> entries = new ArrayList<String>();
        ArrayList<String> sortedEntries = new ArrayList<String>();
        String sortEmptyOutput = String.format("%s is empty, nothing to sort", fileName);
        String sortedOutput = String.format("%s sorted", fileName);
        
        assertEquals(sortEmptyOutput, textBuddy.sortEntries());//empty arraylist
        assertEquals(entries, textBuddy.getDataLines());
        
    	textBuddy.addEntry("apple");
    	textBuddy.addEntry("zebra");
    	textBuddy.addEntry("pool");
    	String[] sortedArray = {"apple", "pool", "zebra"};
    	Collections.addAll(sortedEntries, sortedArray);
    	assertEquals(sortedOutput, textBuddy.sortEntries());
        assertEquals(sortedEntries, textBuddy.getDataLines());
    	
        deleteDummyFile(fileName);
    }
    
    @Test
    public void processSortCommandTest(){
    	String[] testData = new String[0];
    	String fileName = initializeDummyFile(testData);
    	TextBuddy textBuddy = new TextBuddy(fileName);
        TextBuddy.CommandObject validSortCommand = new TextBuddy.CommandObject("Sort");
        TextBuddy.CommandObject invalidSortCommand  = new TextBuddy.CommandObject("Sort 30");
        ArrayList<String> entries = new ArrayList<String>();
        ArrayList<String> sortedEntries = new ArrayList<String>();
        String sortEmptyOutput = String.format("%s is empty, nothing to sort", fileName);
        String sortedOutput = String.format("%s sorted", fileName);
        String invalidCommandOutput = "Invalid command parameter";

        assertEquals(invalidCommandOutput, textBuddy.processSortCommand(invalidSortCommand));
    	
        assertEquals(sortEmptyOutput, textBuddy.processSortCommand(validSortCommand));//empty arraylist
        assertEquals(entries, textBuddy.getDataLines());
        
    	textBuddy.addEntry("apple");
    	textBuddy.addEntry("zebra");
    	textBuddy.addEntry("pool");
    	String[] sortedArray = {"apple", "pool", "zebra"};
    	Collections.addAll(sortedEntries, sortedArray);
    	assertEquals(sortedOutput, textBuddy.processSortCommand(validSortCommand));
        assertEquals(sortedEntries, textBuddy.getDataLines());
        
        deleteDummyFile(fileName);
    }
}
