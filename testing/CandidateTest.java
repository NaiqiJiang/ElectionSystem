/**
 * CandidateTest.java
 * 
 * This file contains the CandidateTest class, designed to perform unit tests on the Candidate class functionality using JUnit.
 * It covers tests for adding votes to a candidate, retrieving a candidate's name, party affiliation, and the correct tally of votes.
 * The setUp method initializes common objects used across different test methods, ensuring each test is conducted with a fresh start.
 * These tests ensure that the Candidate class behaves as expected under various scenarios, demonstrating the application of
 * automated testing in software development to maintain code integrity and functionality.
 * 
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Election.Candidate;
import Election.Party;

public class CandidateTest {
    private Candidate candidate;
    private Party party;

    @Before
    public void setUp() {
        party = new Party("Test Party");
        candidate = new Candidate("John Smith", party);
    }

    @Test
    public void testAddVote() {
        // Before testing, the vote count should be 0
        assertEquals(0, candidate.getVotes());
        // After adding one vote, the number of votes should be 1
        candidate.addVote();
        assertEquals(1, candidate.getVotes());
    }

    @Test
    public void testGetName() {
        //Test whether the candidate name obtained is correct
        assertEquals("John Smith", candidate.getName());
    }

    @Test
    public void testGetParty() {
        // Test whether getting the candidate's party affiliation is correct
        assertEquals(party, candidate.getParty());
        // compare party names to ensure it's the right party
        assertEquals("Test Party", candidate.getParty().getName());
    }

    @Test
    public void testGetVotes() {
        // The initial number of votes should be 0
        assertEquals(0, candidate.getVotes());
        // After adding a few votes, verify whether the number of votes has been added correctly.
        candidate.addVote();
        candidate.addVote();
        assertEquals(2, candidate.getVotes());
    }

    @Test
    public void testAllocateAndCheckSeats() {
        // Initially, the candidate should have no seats
        assertFalse(candidate.hasSeats());
        assertEquals(0, candidate.getSeats());

        // Allocate a seat and check again
        candidate.allocateSeat();
        assertTrue(candidate.hasSeats());

        // Test adding multiple seats
        candidate.addSeats(); // Add one more seat
        assertEquals(1, candidate.getSeats()); // Now total seats should be 2
    }

    @Test
    public void testSetSeats() {
        // Set seats to a specific number and check
        candidate.setSeats(3);
        assertEquals(3, candidate.getSeats());
    }
}
