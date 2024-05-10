/**
 * Represents a political party in an election system.
 * <p>
 * This class maintains information about a party including its name, list of candidates,
 * total votes received, seats allocated, and remaining votes after an initial seat allocation.
 * It supports operations such as adding votes, adding candidates, allocating seats,
 * and calculating quotas for seat distribution.
 * <p>
 * Author: Naiqi Jiang, Ruirui Xu, Jiahao Sun
 */
package Election;


import java.util.ArrayList;
import java.util.List;

public class Party {
    private String name;
    private List<Candidate> candidates;
    private int votes;
    private int seats;
    private int remainderVotes; // Votes remaining after initial seat allocation
    private boolean hasWonSeatInRound; // Indicates if the party has won a seat in the current allocation round
    private int initialAllocationSeats;
    private int secondAllocationSeats;

    /**
     * Constructs a Party with a specific name.
     *
     * @param name The name of the party.
     */
    public Party(String name) {
        this.name = name;
        this.candidates = new ArrayList<>();
        this.votes = 0;
        this.seats = 0;
        this.remainderVotes = 0;
        this.hasWonSeatInRound = false;
    }

    /**
     * Adds a second allocation seat to the party.
     */
    public void addSecondAllocationSeat() {
        this.secondAllocationSeats++;
    }

    /**
     * Returns the number of second allocation seats for the party.
     *
     * @return The number of second allocation seats.
     */
    public int getSecondAllocationSeats() {
        return this.secondAllocationSeats;
    }

    /**
     * Resets the indicator for whether the party has won a seat in the current round, allowing for further seat allocations.
     */
    public void resetWonSeatInRound() {
        this.hasWonSeatInRound = false;
    }

    /**
     * Checks if the party has won a seat in the current allocation round.
     *
     * @return true if the party has won a seat, false otherwise.
     */
    public boolean hasWonSeatInRound() {
        return hasWonSeatInRound;
    }

    /**
     * Sets whether the party has won a seat in the current allocation round.
     *
     * @param decision true if the party has won a seat, false otherwise.
     */
    public void setWonSeatInRound(boolean decision) {
        this.hasWonSeatInRound = decision;
    }

    /**
     * Adds a seat to the party's total count.
     */
    public void addSeats() {
        this.seats += 1;
    }

    /**
     * Adds a candidate to the party's list of candidates.
     *
     * @param candidate The candidate to add.
     */
    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
    }

    /**
     * Increments the total votes of the party by one.
     */
    public void addVote() {
        votes++;
    }

    /**
     * Sets the number of initial seats allocated to the party based on voting results.
     *
     * @param seats The number of initial seats to set.
     */
    public void setInitialSeats(int seats) {
        this.initialAllocationSeats = seats;
    }

    /**
     * Returns the number of initial seats allocated to the party.
     *
     * @return The number of initial seats.
     */
    public int getInitialSeats() {
        return this.initialAllocationSeats;
    }

    /**
     * Sets the number of votes remaining after the initial seat allocation.
     *
     * @param remainderVotes The number of remainder votes to set.
     */
    public void setRemainderVotes(int remainderVotes) {
        this.remainderVotes = remainderVotes;
    }

    /**
     * Returns the total number of votes received by the party.
     *
     * @return The total votes.
     */
    public int getVotes() {
        return votes;
    }

    /**
     * Returns the total number of seats allocated to the party.
     *
     * @return The total seats.
     */
    public int getSeats() {
        return initialAllocationSeats + secondAllocationSeats;
    }

    /**
     * Reduces the number of seats of the party by one.
     */
    public void decreaseSeats() {
        this.seats -= 1;
    }

    /**
     * Returns the number of votes remaining after the initial seat allocation.
     *
     * @return The remainder votes.
     */
    public int getRemainderVotes() {
        return remainderVotes;
    }

    /**
     * Returns the name of the party.
     *
     * @return The name of the party.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of candidates affiliated with the party.
     *
     * @return A list of candidates.
     */
    public List<Candidate> getCandidates() {
        return candidates;
    }

    /**
     * Sets the total number of votes received by the party.
     *
     * @param votes The total number of votes to set.
     */
    public void setVotes(int votes) {
        this.votes = votes;
    }

    /**
     * Checks if a specific candidate is part of the party.
     *
     * @param candidateName The name of the candidate to check.
     * @return true if the candidate is part of the party, false otherwise.
     */
    public boolean hasCandidate(String candidateName) {
        for (Candidate candidate : candidates) {
            if (candidate.getName().equals(candidateName)) {
                return true;
            }
        }
        return false;
    }
}
