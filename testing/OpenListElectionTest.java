/**
 * OpenListElectionTest.java
 * 
 * This file contains the OpenListElectionTest class, which is used to test the functionality
 * of the OpenListElection class with JUnit. The tests cover a range of scenarios including
 * the correct tallying of votes for individual candidates and parties, the allocation of seats
 * based on those votes, and handling of edge cases such as empty ballots and invalid input parameters.
 * These unit tests are essential to ensure that the OpenListElection class performs as expected,
 * accurately reflecting the outcome of an open list election process. The suite demonstrates
 * rigorous testing of the main components and critical paths of the election logic within
 * a controlled test environment.
 *
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Election.Candidate;
import Election.OpenListElection;
import Election.Party;

public class OpenListElectionTest {
    private OpenListElection election;
    private Party partyA;
    private Party partyB;
    private Candidate candidate1;
    private Candidate candidate2;
    private Candidate candidate3;
    private Candidate candidate4;

    @Before
    public void setUp() {
        // Initialize parties and candidates
        partyA = new Party("Party A");
        partyB = new Party("Party B");

        candidate1 = new Candidate("Candidate 1", partyA);
        candidate2 = new Candidate("Candidate 2", partyA);
        candidate3 = new Candidate("Candidate 3", partyB);
        candidate4 = new Candidate("Candidate 4", partyB);

        partyA.addCandidate(candidate1);
        partyA.addCandidate(candidate2);
        partyB.addCandidate(candidate3);
        partyB.addCandidate(candidate4);

        // Create an election with 4 total seats
        election = new OpenListElection(4, 100);
        election.addParty(partyA);
        election.addParty(partyB);
    }

    @Test
    public void testConductElection() {
        List<String> ballots = Arrays.asList(
            "1,0,1,0", // Votes for candidate1 and candidate3
            "1,0,0,1", // Votes for candidate1 and candidate4
            "0,1,1,0"  // Votes for candidate2 and candidate3
        );

        election.conductElection(ballots);

        // Check total votes per candidate
        Assert.assertEquals(2, candidate1.getVotes());
        Assert.assertEquals(1, candidate2.getVotes());
        Assert.assertEquals(2, candidate3.getVotes());
        Assert.assertEquals(1, candidate4.getVotes());

        // Check total votes per party
        Assert.assertEquals(3, partyA.getVotes());
        Assert.assertEquals(3, partyB.getVotes());

        // Check seat allocation
        Assert.assertTrue("Party A should have at least 1 seat", partyA.getSeats() > 0);
        Assert.assertTrue("Party B should have at least 1 seat", partyB.getSeats() > 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSeats() {
        new OpenListElection(0, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeVotes() {
        new OpenListElection(1, -1);
    }

    @Test
    public void testEdgeCaseEmptyBallot() {
        List<String> ballots = Arrays.asList(
            "",
            " " 
        );

        election.conductElection(ballots);

        // Ensure no votes have been counted
        Assert.assertEquals(0, partyA.getVotes());
        Assert.assertEquals(0, partyB.getVotes());
    }
}
