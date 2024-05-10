/**
 * PartyTest.java
 * 
 * This file contains the PartyTest class, which is structured to verify the functionality
 * of the Party class through JUnit testing. It includes a comprehensive suite of tests that
 * examine the behavior of the Party class, such as adding candidates, tallying votes,
 * setting and retrieving initial and secondary allocation seats, as well as managing
 * remainder votes and seat allocation flags. By simulating various scenarios and asserting
 * the expected outcomes, these tests ensure that the Party class logic accurately handles
 * the election process details. The effectiveness of the class methods in maintaining
 * correct states and handling input is thus demonstrated, showcasing the importance
 * of unit tests in ensuring software quality and correctness.
 *
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Election.Candidate;
import Election.Party;

public class PartyTest {
    private Party party;
    private Candidate candidate;

    @Before
    public void setUp() {
        party = new Party("Test Party"); // Initialize a new Party with the name "Test Party"
        candidate = new Candidate("Candidate 1", party); // Assume Candidate constructor takes (String, Party)
    }

    // Test adding a candidate to the party
    @Test
    public void testAddCandidate() {
        party.addCandidate(candidate); // Add the candidate to the party
        assertTrue("Party should contain the added candidate", party.getCandidates().contains(candidate));
    }

    // Test adding votes and checking vote count
    @Test
    public void testAddVote() {
        assertEquals("Initial votes should be 0", 0, party.getVotes());
        party.addVote(); // Add a vote to the party
        assertEquals("Party votes should be incremented", 1, party.getVotes());
    }

    // Test setting and getting initial seats
    @Test
    public void testInitialSeats() {
        party.setInitialSeats(5);
        assertEquals("Initial seats should be set correctly", 5, party.getInitialSeats());
    }

    // Test second allocation seat addition
    @Test
    public void testAddSecondAllocationSeat() {
        assertEquals("Initial second allocation seats should be 0", 0, party.getSecondAllocationSeats());
        party.addSecondAllocationSeat();
        assertEquals("Second allocation seats should increment by 1", 1, party.getSecondAllocationSeats());
    }

    // Test setting and checking remainder votes
    @Test
    public void testRemainderVotes() {
        party.setRemainderVotes(10);
        assertEquals("Remainder votes should be set correctly", 10, party.getRemainderVotes());
    }

    // Test setting and getting total seats after initial and second allocations
    @Test
    public void testTotalSeatsCalculation() {
        party.setInitialSeats(2);
        party.addSecondAllocationSeat(); // Adds one second allocation seat
        assertEquals("Total seats should equal initial plus second allocation seats", 3, party.getSeats());
    }

    // Test setting the won seat flag and resetting it
    @Test
    public void testWonSeatInRound() {
        party.setWonSeatInRound(true);
        assertTrue("Party should have won seat in round", party.hasWonSeatInRound());
        party.resetWonSeatInRound();
        assertFalse("Party should have reset won seat in round", party.hasWonSeatInRound());
    }
}
