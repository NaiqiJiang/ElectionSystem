/**
 * MPOMVTest.java
 *
 * This class contains the MPOMVTest suite designed to rigorously test the Multi-Party Open-Mixed Voting (MPOMV)
 * system implemented in the MPOMV class. The suite includes various test scenarios that ensure the system
 * accurately processes voting data, calculates seats correctly based on votes, and handles different
 * voting and ballot configurations. Key areas tested include initialization validity checks, vote tallying accuracy,
 * seat allocation correctness, and the proper handling of edge cases such as no votes or excess ballots.
 * These tests are crucial for validating the functionality and reliability of the MPOMV voting system under
 * a variety of conditions, ensuring the integrity of the election process.
 *
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import Election.Candidate;
import Election.MPOMV;
import Election.Party;

public class MPOMVTest {
    private MPOMV election;
    private Candidate candidate1;
    private Candidate candidate2;
    private Candidate candidate3;

    @Before
    public void setUp() {
        candidate1 = new Candidate("Candidate1", new Party("Party1"));
        candidate2 = new Candidate("Candidate2", new Party("Party2"));
        candidate3 = new Candidate("Candidate3", new Party("Party3"));
        List<Candidate> candidates = Arrays.asList(candidate1, candidate2, candidate3);
        election = new MPOMV(2, 100, candidates);  // 2 seats available
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTotalSeats() {
        new MPOMV(0, 100, Arrays.asList(candidate1));  // Should throw IllegalArgumentException
    }

    @Test
    public void testElectionInitialization() {
        assertEquals("Should have 3 candidates.", 3, election.getCandidates().size());
        assertEquals("Total seats should be 2.", 2, election.getTotalSeats());
        assertEquals("Total votes should be 100.", 100, election.getTotalVotes());
    }

    @Test
    public void testConductElection_NoVotes() {
        election = new MPOMV(2, 0, Arrays.asList(candidate1, candidate2));
        election.conductElection(Arrays.asList());  // No ballots
        for (Candidate candidate : election.getCandidates()) {
            assertEquals("No votes should result in zero votes for each candidate.", 0, candidate.getVotes());
        }
    }

    @Test
    public void testConductElection_WithVotes() {
        List<String> ballots = Arrays.asList("1,0,0", "0,1,0", "0,0,1", "1,0,0");
        election.conductElection(ballots);
        assertEquals("Candidate1 should receive 2 votes.", 2, candidate1.getVotes());
        assertEquals("Candidate2 should receive 1 vote.", 1, candidate2.getVotes());
        assertEquals("Candidate3 should receive 1 vote.", 1, candidate3.getVotes());
    }

    @Test
    public void testCalculateSeats() {
        candidate1.addVote();
        candidate1.addVote();  // 2 votes
        candidate2.addVote();  // 1 vote
        candidate3.addVote();  // 1 vote
        election.calculateSeats();
        assertTrue("Candidate1 should have seats.", candidate1.getSeats() > 0);
        assertTrue("Candidate3 should not have seats.", candidate3.getSeats() == 0);
    }
    
    @Test
    public void testConductElection_ExtraVotesIgnored() {
        List<String> ballots = Arrays.asList("1,0,0,1"); // Extra vote should be ignored
        election.conductElection(ballots);
        assertEquals("Candidate1 should receive 1 vote.", 1, candidate1.getVotes());
        assertEquals("Candidate2 should receive 0 votes.", 0, candidate2.getVotes());
        assertEquals("Candidate3 should receive 0 votes.", 0, candidate3.getVotes());
    }
    
    @Test
    public void testConductElection_MissingVotesHandled() {
        List<String> ballots = Arrays.asList("1"); // Missing votes for Candidate2 and Candidate3
        election.conductElection(ballots);
        assertEquals("Candidate1 should receive 1 vote.", 1, candidate1.getVotes());
        assertEquals("Candidate2 should receive 0 votes.", 0, candidate2.getVotes());
        assertEquals("Candidate3 should receive 0 votes.", 0, candidate3.getVotes());
    }
    
    @Test
    public void testAllocateSeats_AllSeatsAllocated() {
        candidate1.addVote();
        candidate1.addVote();
        candidate1.addVote();  // 3 votes
        candidate2.addVote();  // 1 vote
        candidate3.addVote();  // 1 vote
        election.calculateSeats();
        assertEquals("Candidate1 should have 1 seat.", 1, candidate1.getSeats());
        assertEquals("Candidate2 should have 1 seat.", 1, candidate2.getSeats());
        assertEquals("Candidate3 should have 0 seats.", 0, candidate3.getSeats());
    }
    
    @Test
    public void testAllocateSeats_EnsureFairAllocationWithTies() {
        candidate1.addVote();
        candidate1.addVote();
        candidate2.addVote();
        candidate2.addVote();
        candidate3.addVote();
        election.calculateSeats();
        // Ensure that seats are fairly allocated, possibly needing a randomized or prioritized tie-breaking mechanism
        int totalSeatsAllocated = candidate1.getSeats() + candidate2.getSeats() + candidate3.getSeats();
        assertEquals("Total allocated seats should equal total available seats.", 2, totalSeatsAllocated);
    }
}