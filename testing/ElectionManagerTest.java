/**
 * ElectionManagerTest.java
 * 
 * This file contains the ElectionManagerTest class, designed to rigorously test the ElectionManager class functionality using JUnit.
 * The tests encompass scenarios such as loading ballot data from files, exporting audit files, and handling non-existent files. 
 * These tests ensure that the ElectionManager can correctly parse and process election data, generate accurate audit logs, 
 * and robustly handle errors and exceptional conditions. Through these tests, the class demonstrates the application of test-driven 
 * development practices to ensure the reliability and integrity of the election management process within a simulated election system.
 * 
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import Election.Candidate;
import Election.Election;
import Election.ElectionManager;
import Election.Party;

public class ElectionManagerTest {
    private ElectionManager manager;
    private String FirstCplFilePath = "testing/CPL_Voting_1.csv";
    private String SecondCplFilePath = "testing/CPL_Voting_2.csv";
    private String FirstOplFilePath = "testing/OPL_Voting_1.csv";
    private String SecondOplFilePath = "testing/OPL_Voting_2.csv";
    private String FirstMpoFilePath = "testing/MPO_Voting_1.csv";
    private String SecondMpoFilePath = "testing/MPO_Voting_2.csv";
    private String FirstMVFilePath = "testing/MV_Voting_1.csv";
    private String SecondMVFilePath = "testing/MV_Voting_2.csv";

    @Before
    public void setUp() throws IOException {
        manager = new ElectionManager(null);
    }

    @Test
    public void testLoadSingleCplFile() throws IOException {
        manager.loadBallotData(FirstCplFilePath);
        Election election = manager.getElection();
        manager.conductElection();
        assertNotNull("Election should not be null after loading data", election);
        assertEquals("Should have 6 parties", 6, election.getParties().size());
        assertEquals("Should have a total of 9 votes", 9, election.getTotalVotes());
        assertEquals("Should have 3 seats", 3, election.getTotalSeats());

        // Create a map of expected votes for each party
        Map<String, Integer> expectedVotes = new HashMap<>();
        expectedVotes.put("Democratic", 3);
        expectedVotes.put("Republican", 3);
        expectedVotes.put("New Wave", 2);
        expectedVotes.put("Reform", 1);
        expectedVotes.put("Green", 0);
        expectedVotes.put("Independent", 0);

        // Iterate through the parties and check votes
        for (Party party : election.getParties()) {
            String partyName = party.getName();
            int votes = party.getVotes();
            assertTrue("Election should contain " + partyName, expectedVotes.containsKey(partyName));
            assertEquals(partyName + " party should have correct number of votes",
                         (int) expectedVotes.get(partyName), votes);
        }
    }

    @Test
    public void testLoadMultipleCplFile() throws IOException {
        manager.loadBallotData(FirstCplFilePath);
        manager.loadBallotData(SecondCplFilePath);
        Election election = manager.getElection();
        manager.conductElection();
        assertNotNull("Election should not be null after loading data", election);
        assertEquals("Should have 6 parties", 6, election.getParties().size());
        assertEquals("Should have a total of 59 votes", 59, election.getTotalVotes());
        assertEquals("Should have 8 seats", 8, election.getTotalSeats());

        // Create a map of expected votes for each party
        Map<String, Integer> expectedVotes = new HashMap<>();
        expectedVotes.put("Democratic", 13);
        expectedVotes.put("Republican", 13);
        expectedVotes.put("New Wave", 12);
        expectedVotes.put("Reform", 11);
        expectedVotes.put("Green", 10);
        expectedVotes.put("Independent", 0);

        // Iterate through the parties and check votes
        for (Party party : election.getParties()) {
            String partyName = party.getName();
            int actualVotes = party.getVotes();
            assertTrue("Election should contain " + partyName, expectedVotes.containsKey(partyName));
            assertEquals("Party " + partyName + " should have correct number of votes",
                         (int) expectedVotes.get(partyName), actualVotes);
        }
    }

    @Test
    public void testCheckPartySeats() {
        manager.loadBallotData(FirstCplFilePath);
        manager.loadBallotData(SecondCplFilePath);
        manager.conductElection();

        Election election = manager.getElection();
        assertNotNull("Election should not be null after loading data", election);
        assertEquals("Should have 6 parties", 6, election.getParties().size());
        assertEquals("Should have a total of 59 votes", 59, election.getTotalVotes());
        assertEquals("Should have 8 seats", 8, election.getTotalSeats());

        // Create a map of expected seats for each party
        Map<String, Integer[]> expectedSeats = new HashMap<>();
        expectedSeats.put("Democratic",    new Integer[] {1, 1, 2}); // {initial seats, second allocation seats, total seats}
        expectedSeats.put("Republican",    new Integer[] {1, 1, 2});
        expectedSeats.put("New Wave",      new Integer[] {1, 1, 2});
        expectedSeats.put("Reform",        new Integer[] {1, 0, 1});
        expectedSeats.put("Green",         new Integer[] {1, 0, 1});
        expectedSeats.put("Independent",   new Integer[] {0, 0, 0});

        // Iterate through the parties and check seats
        for (Party party : election.getParties()) {
            String partyName = party.getName();
            Integer[] actualSeats = new Integer[] {
                party.getInitialSeats(),
                party.getSecondAllocationSeats(),
                party.getSeats() // Total seats should equal the sum of initial and second allocation seats
            };

            assertTrue("Election should contain " + partyName, expectedSeats.containsKey(partyName));
            assertArrayEquals("Party " + partyName + " should have correct number of seats",
                            expectedSeats.get(partyName), actualSeats);
        }
    }

    @Test
    public void testSingleOplFile() throws IOException {
        manager.loadBallotData(FirstOplFilePath);  // Update this path to your actual file
        manager.conductElection();

        Election election = manager.getElection();
        assertNotNull("Election should not be null after loading data", election);
        assertEquals("Should have 3 parties", 3, election.getParties().size());
        assertEquals("Should have a total of 12 votes", 12, election.getTotalVotes());
        assertEquals("Should have 3 seats", 3, election.getTotalSeats());

        // Expected votes for each party
        Map<String, Integer> expectedVotes = new HashMap<>();
        expectedVotes.put("Democrat", 6);
        expectedVotes.put("Republican", 4);
        expectedVotes.put("Independent", 2);

        // Expected seats for each party
        Map<String, Integer> expectedSeats = new HashMap<>();
        expectedSeats.put("Democrat", 1);
        expectedSeats.put("Republican", 1);
        expectedSeats.put("Independent", 1);
    }

    @Test
    public void testCheckMultipleOplFile() {
        manager.loadBallotData(FirstOplFilePath);
        manager.loadBallotData(SecondOplFilePath);
        manager.conductElection();
    
        Election election = manager.getElection();
        assertNotNull("Election should not be null after loading data", election);
        assertEquals("Should have 3 parties", 3, election.getParties().size());
        assertEquals("Should have a total of 62 votes", 62, election.getTotalVotes());
        assertEquals("Should have 5 seats", 5, election.getTotalSeats());
    
        // Create a map of expected seats for each party
        Map<String, Integer[]> expectedSeats = new HashMap<>();
        expectedSeats.put("Democrat",    new Integer[] {3, 0, 3}); // {initial seats, second allocation seats, total seats}
        expectedSeats.put("Republican",  new Integer[] {0, 2, 2});
        expectedSeats.put("Independent", new Integer[] {0, 0, 0});
    
        // Iterate through the parties and check seats
        for (Party party : election.getParties()) {
            String partyName = party.getName();
            Integer[] actualSeats = new Integer[] {
                party.getInitialSeats(),
                party.getSecondAllocationSeats(),
                party.getSeats() // Total seats should equal the sum of initial and second allocation seats
            };
    
            assertTrue("Election should contain " + partyName, expectedSeats.containsKey(partyName));
            assertArrayEquals("Party " + partyName + " should have correct number of seats",
                              expectedSeats.get(partyName), actualSeats);
        }
    
        // Check that the correct candidates have won the seats
        List<String> expectedWinners = Arrays.asList("Pike", "Lucy", "John", "Etta", "Alawa");
        for (String winner : expectedWinners) {
            assertTrue("Candidate " + winner + " should have won a seat",
                       hasCandidateWonSeat(election, winner));
        }
    }
    
    private boolean hasCandidateWonSeat(Election election, String candidateName) {
        return election.getParties().stream()
            .flatMap(p -> p.getCandidates().stream())
            .anyMatch(c -> c.getName().equals(candidateName) && c.hasSeats());
    }
    
    @Test
    public void testSingleMpoFile() throws IOException {
        manager.loadBallotData(FirstMpoFilePath);  // Update this to your actual file path
        manager.conductElection();

        Election election = manager.getElection();
        assertNotNull("Election result should not be null", election);
        assertEquals("Total number of votes should be 9", 9, election.getTotalVotes());
        assertEquals("Total number of seats should be 2", 2, election.getTotalSeats());

        // Expected votes and seats for each candidate across all parties
        Map<String, Integer[]> expectedResults = new HashMap<>();
        expectedResults.put("Anna", new Integer[] {3, 1});  // Votes, seats
        expectedResults.put("Foster", new Integer[] {2, 1});
        expectedResults.put("Borg", new Integer[] {2, 0});
        expectedResults.put("Jones", new Integer[] {1, 0});
        expectedResults.put("Smith", new Integer[] {1, 0});
        expectedResults.put("Deutsch", new Integer[] {0, 0});

        // Check votes and seats for each candidate and validate candidate count per party
        for (Party party : election.getParties()) {
            for (Candidate candidate : party.getCandidates()) {
                String candidateName = candidate.getName();
                assertTrue("Election should contain " + candidateName, expectedResults.containsKey(candidateName));
                Integer[] results = expectedResults.get(candidateName);
                assertEquals(candidateName + " should have correct number of votes", results[0], (Integer) candidate.getVotes());
                assertEquals(candidateName + " should have correct number of seats", results[1], (Integer) candidate.getSeats());
            }
        }
    }

    @Test
    public void testMultipleMpoFiles() throws IOException {
        manager.loadBallotData(FirstMpoFilePath);
        manager.loadBallotData(SecondMpoFilePath);
        manager.conductElection();

        Election election = manager.getElection();
        assertNotNull("Election result should not be null", election);
        assertEquals("Type of Election should be MPO", "MPO", election.getElectionType());
        assertEquals("Total number of seats should be 5", 5, election.getTotalSeats());
        assertEquals("Total number of votes should be 20", 20, election.getTotalVotes());

        // Expected results for seat winners and votes
        Map<String, Integer[]> expectedResults = new HashMap<>();
        expectedResults.put("Anna", new Integer[] {6, 1});    // Votes, seats
        expectedResults.put("Foster", new Integer[] {2, 1});
        expectedResults.put("Borg", new Integer[] {2, 1});
        expectedResults.put("Bob", new Integer[] {2, 1});
        expectedResults.put("Cara", new Integer[] {2, 1});
        expectedResults.put("Don", new Integer[] {2, 0});
        expectedResults.put("Eva", new Integer[] {2, 0});
        expectedResults.put("Jones", new Integer[] {1, 0});
        expectedResults.put("Smith", new Integer[] {1, 0});
        expectedResults.put("Deutsch", new Integer[] {0, 0});

        // Check votes and seats for each candidate
        for (Party party : election.getParties()) {
            for (Candidate candidate : party.getCandidates()) {
                String candidateName = candidate.getName();
                assertTrue("Election should contain " + candidateName, expectedResults.containsKey(candidateName));
                Integer[] results = expectedResults.get(candidateName);
                assertEquals(candidateName + " should have correct number of votes", results[0], (Integer) candidate.getVotes());
                assertEquals(candidateName + " should have correct number of seats", results[1], (Integer) candidate.getSeats());
            }
        }
    }

    @Test
    public void testSingleMvFile() throws IOException {
        manager.loadBallotData(FirstMVFilePath);  // Update this to your actual file path
        manager.conductElection();

        Election election = manager.getElection();
        assertNotNull("Election result should not be null", election);
        assertEquals("Type of Election should be MV", "MV", election.getElectionType());
        assertEquals("Total number of seats should be 3", 3, election.getTotalSeats());
        assertEquals("Total number of votes should be 9", 9, election.getTotalVotes());

        // Expected results for seat winners and their votes
        Map<String, Integer[]> expectedResults = new HashMap<>();
        expectedResults.put("Pike", new Integer[] {4, 1});    // Votes, seats
        expectedResults.put("Deutsch", new Integer[] {4, 1});
        expectedResults.put("Borg", new Integer[] {4, 1});
        expectedResults.put("Jones", new Integer[] {4, 0});
        expectedResults.put("Foster", new Integer[] {3, 0});
        expectedResults.put("Smith", new Integer[] {3, 0});

        // Check votes and seats for each candidate
        for (Party party : election.getParties()) {
            for (Candidate candidate : party.getCandidates()) {
                String candidateName = candidate.getName();
                assertTrue("Election should contain " + candidateName, expectedResults.containsKey(candidateName));
                Integer[] results = expectedResults.get(candidateName);
                assertEquals(candidateName + " should have correct number of votes", results[0], (Integer) candidate.getVotes());
                assertEquals(candidateName + " should have correct number of seats", results[1], (Integer) candidate.getSeats());
            }
        }
    }

    @Test
    public void testMultipleMvFiles() throws IOException {
        // Array of file paths for multiple MV files
        manager.loadBallotData(FirstMVFilePath);
        manager.loadBallotData(SecondMVFilePath);
        manager.conductElection();

        Election election = manager.getElection();
        assertNotNull("Election result should not be null", election);
        assertEquals("Type of Election should be MV", "MV", election.getElectionType());
        assertEquals("Total number of seats should be 6", 6, election.getTotalSeats());
        assertEquals("Total number of votes should be 18", 18, election.getTotalVotes());

        // Expected results for seat winners and their votes
        Map<String, Integer[]> expectedResults = new HashMap<>();
        expectedResults.put("Deutsch", new Integer[] {8, 1});  // Votes, seats
        expectedResults.put("Borg", new Integer[] {8, 1});
        expectedResults.put("Jones", new Integer[] {8, 1});
        expectedResults.put("Foster", new Integer[] {6, 1});
        expectedResults.put("Smith", new Integer[] {6, 1});
        expectedResults.put("Pike", new Integer[] {4, 1});
        expectedResults.put("John", new Integer[] {4, 0});

        // Check votes and seats for each candidate
        for (Party party : election.getParties()) {
            for (Candidate candidate : party.getCandidates()) {
                String candidateName = candidate.getName();
                assertTrue("Election should contain " + candidateName, expectedResults.containsKey(candidateName));
                Integer[] results = expectedResults.get(candidateName);
                assertEquals(candidateName + " should have correct number of votes", results[0], (Integer) candidate.getVotes());
                assertEquals(candidateName + " should have correct number of seats", results[1], (Integer) candidate.getSeats());
            }
        }
    }
    @Test
    public void testStateAfterDataLoad() throws IOException {
        manager.loadBallotData(FirstCplFilePath);
        assertNotNull("Loaded election should not be null", manager.getElection());
        assertTrue("Total votes should be updated", manager.getElection().getTotalVotes() > 0);
        assertTrue("Total seats should be updated", manager.getElection().getTotalSeats() > 0);
    }

    @Test
    public void testAuditFileGeneration() throws IOException {
        manager.loadBallotData(FirstCplFilePath);
        manager.conductElection();
        String auditFilePath = "audit.txt";
        manager.exportAuditFile(auditFilePath);
        File auditFile = new File(auditFilePath);
        assertTrue("Audit file should exist", auditFile.exists());
        assertTrue("Audit file should not be empty", auditFile.length() > 0);

        auditFile.delete();
    }

    @Test
    public void testLoadNonexistentFile() {
        String nonexistentFilePath = "path/to/nonexistent/file.csv";
        manager.loadBallotData(nonexistentFilePath);
        // 检查是否有错误消息被打印或者确保没有选举对象被创建
        assertNull("Election should not be created for nonexistent files", manager.getElection());
        // 还可以检查是否有特定的日志输出或错误标记被设置，如果你的方法中包含了这些逻辑
    }
    
}