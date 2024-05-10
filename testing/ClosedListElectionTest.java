/**
 * ClosedListElectionTest.java
 * 
 * This file contains the ClosedListElectionTest class, which is designed to test the functionality of the ClosedListElection class
 * using JUnit. It includes tests for various scenarios, including conducting an election with no votes, with votes leading to seats
 * being allocated, calculating seats based on votes, and resolving ties through a random selection process. These tests ensure the
 * ClosedListElection class accurately allocates seats to parties based on the votes they receive and handles edge cases such as ties
 * as expected. The implementation demonstrates the use of unit testing to verify the correctness and reliability of election logic
 * within a simulated environment.
 * 
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */
import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

import Election.ClosedListElection;
import Election.Party;

public class ClosedListElectionTest {

    @Test
    public void testConductElection_NoVotes_NoSeatsAllocated() {
        ClosedListElection election = new ClosedListElection(5, 0);
        Party party = new Party("Party");
        election.addParty(party);

        // Conducting the election with no votes.
        election.conductElection(Collections.emptyList());

        // Verifying that no seats are allocated to the party.
        assertEquals(0, party.getSeats());
    }

    @Test
    public void testConductElection_WithVotes_SeatsAllocated() {
        ClosedListElection election = new ClosedListElection(5, 100);
        Party party = new Party("Party");
        election.addParty(party);
        party.setVotes(100); // Setting up the scenario where the party receives all the votes.

        // Conducting the election.
        election.conductElection(Collections.singletonList("1"));

         // Asserting that all seats are allocated to the party receiving all votes.
        assertEquals("All votes to one party should give all seats to that party", 5, party.getSeats());
    }

    @Test
    public void testCalculateSeats() {
        ClosedListElection election = new ClosedListElection(5, 100);
        Party party = new Party("Party");
        election.addParty(party);
        party.setVotes(20); // Simulating a distribution of votes to the party.

        /// Calculating seats based on the given votes.
        election.calculateSeats();

        // Asserting the party receives at least one seat.
        assertTrue("Party should have at least one seat", party.getSeats() > 0);
    }

    @Test
    public void testResolveTieByRandom() {
        ClosedListElection election = new ClosedListElection(5, 100);
        Party party1 = new Party("Party1");
        Party party2 = new Party("Party2");
        election.addParty(party1);
        election.addParty(party2);
        party1.setVotes(50); // Setting equal votes for both parties to simulate a tie.
        party2.setVotes(50); // Equal votes to both parties

        // Calculating seats, expecting the tie resolution mechanism to kick in.
        election.calculateSeats();

        // Verifying that all seats are distributed between the two parties.
        int totalSeatsAllocated = party1.getSeats() + party2.getSeats();
        assertEquals("Total seats should be allocated even in a tie", 5, totalSeatsAllocated);
    }

    public void testNegativeTotalSeats() {
        try {
            new ClosedListElection(-1, 100);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Total seats must be greater than 0", e.getMessage());
        }
    }

    @Test
    public void testNegativeTotalVotes() {
        try {
            new ClosedListElection(5, -100);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Total Votes must be greater than 0", e.getMessage()); // This assumes you add a similar check and message for total votes in your constructor
        }
    }
}
