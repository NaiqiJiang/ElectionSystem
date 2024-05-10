/**
 * SystemTest.java
 *
 * This file hosts the SystemTest class, which conducts end-to-end tests on the ElectionManager class across
 * various election formats like CPL, OPL, MPO, and MV. These tests ensure the system properly handles the
 * complete election process from loading ballot data, executing the election, to generating final audit reports.
 * Each test simulates real-world usage scenarios to ensure all components function together seamlessly and produce
 * accurate results, verifying the system's ability to manage and finalize elections reliably.
 *
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import Election.ElectionManager;

public class SystemTest {

    /**
     * Test the Closed Party List (CPL) election process using sample data files.
     * This test loads multiple CPL voting data files, conducts an election, and checks if the audit file contains the expected contents.
     */
    @Test
    public void testCPLElectionProcess() {
        ElectionManager manager = new ElectionManager(null); // Initialize ElectionManager without a specific election type
        String[] filenames = {"TestFile/CPL_Voting_1.csv", "TestFile/CPL_Voting_2.csv"}; // Files containing voting data
        String outputFilename = "audit.txt"; // Filename for the generated audit report

        // Load ballot data from all specified files and conduct the election
        for (String filename : filenames) {
            manager.loadBallotData(filename);
        }
        manager.conductElection();
        manager.finalizeElectionResults();
        manager.exportAuditFile(outputFilename);  // Export results to an audit file

        // Assert that the audit file contains specific strings indicating correct processing
        assertFileContains(outputFilename, "Type of Election: CPL");
        assertFileContains(outputFilename, "Party");
        assertFileContains(outputFilename, "Votes");
        assertFileContains(outputFilename, "List of Seat Winners and their Party Affiliation");
    }

    /**
     * Test the Open Party List (OPL) election process.
     * This test performs an OPL election using a sample data file and verifies that the audit file includes the expected details.
     */
    @Test
    public void testOPLElectionProcess() {
        ElectionManager manager = new ElectionManager(null);
        String filename = "TestFile/OPL_Voting_1.csv";
        String outputFilename = "OPL_audit.txt";

        // Load voting data, conduct the election, finalize results, and export to an audit file
        manager.loadBallotData(filename);
        manager.conductElection();
        manager.finalizeElectionResults();
        manager.exportAuditFile(outputFilename);

        // Verify that the audit file contains key sections and information
        assertFileContains(outputFilename, "Type of Election: OPL");
        assertFileContains(outputFilename, "List of Seat Winners and their Party Affiliation");
    }

    /**
     * Test the Multi-Party Open-Mixed Voting (MPO) election process.
     * This method checks the MPO election process from data loading to audit file generation.
     */
    @Test
    public void testMPOElectionProcess() {
        ElectionManager manager = new ElectionManager(null);
        String filename = "TestFile/MPO_Voting_1.csv";
        String outputFilename = "MPO_audit.txt";

        // Conduct the election and generate the audit report
        manager.loadBallotData(filename);
        manager.conductElection();
        manager.finalizeElectionResults();
        manager.exportAuditFile(outputFilename);

        // Ensure the audit file reflects the correct election type and details
        assertFileContains(outputFilename, "Type of Election: MPO");
        assertFileContains(outputFilename, "List of Seat Winners and their Party Affiliation");
    }

    /**
     * Test the Majority Vote (MV) election process.
     * This test covers the election flow for MV, confirming output in the generated audit file.
     */
    @Test
    public void testMVElectionProcess() {
        ElectionManager manager = new ElectionManager(null);
        String filename = "TestFile/MV_Voting_1.csv";
        String outputFilename = "MV_audit.txt";

        // Load the data, process the election, finalize results, and export them to a file
        manager.loadBallotData(filename);
        manager.conductElection();
        manager.finalizeElectionResults();
        manager.exportAuditFile(outputFilename);

        // Verify that the audit file includes necessary information about the election
        assertFileContains(outputFilename, "Type of Election: MV");
        assertFileContains(outputFilename, "List of Seat Winners and their Party Affiliation");
    }

    /**
     * Helper method to verify if a text file contains a specified string.
     * @param filename The name of the file to be checked.
     * @param text The string to search for in the file.
     */
    private void assertFileContains(String filename, String text) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains(text)) {
                    found = true;
                    break;
                }
            }
            assertTrue("File '" + filename + "' should contain '" + text + "'", found);
        } catch (IOException e) {
            fail("Failed to read file '" + filename + "': " + e.getMessage());
        }
    }
}
