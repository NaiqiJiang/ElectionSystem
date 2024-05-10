/**
 * Represents a candidate in an election.
 * <p>
 * This class encapsulates the properties and behaviors of a candidate, including their name,
 * the party they are affiliated with, the number of votes they have received, and their seat allocations.
 * It provides functionality to manage votes and seat assignments, demonstrating encapsulation and abstraction principles.
 * <p>
 * Author: Naiqi Jiang, Ruirui Xu, Jiahao Sun
 */
package Election;

public class Candidate {
    private String name;
    private Party party;
    private int votes;
    private boolean hadSeats = false;
    private int seats;

    /**
     * Constructs a new candidate with the specified name and party affiliation.
     * The candidate starts with zero votes and no seats.
     *
     * @param name  the name of the candidate
     * @param party the party to which the candidate belongs
     */
    public Candidate(String name, Party party) {
        this.name = name;
        this.party = party;
        this.votes = 0;
    }

    /**
     * Constructs a new candidate with only a name specified, no party affiliation.
     * The candidate starts with zero votes and no seats.
     *
     * @param name the name of the candidate
     */
    public Candidate(String name) {
        this.name = name;
    }

    /**
     * Allocates a seat to this candidate, marking them as having at least one seat.
     */
    public void allocateSeat(){
        this.hadSeats = true;
    }

    /**
     * Checks if the candidate has any seats.
     *
     * @return true if the candidate has at least one seat, false otherwise
     */
    public boolean hasSeats(){
        return hadSeats;
    }

    /**
     * Sets the number of votes for this candidate.
     *
     * @param votes the number of votes to set
     */
    public void setVotes(int votes) {
        this.votes = votes;
    }

    /**
     * Increments the vote count for this candidate by one.
     */
    public void addVote() {
        votes++;
    }

    /**
     * Returns the name of the candidate.
     *
     * @return the name of the candidate
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the party affiliation of the candidate.
     *
     * @return the party of the candidate
     */
    public Party getParty() {
        return party;
    }

    /**
     * Returns the total number of votes received by the candidate.
     *
     * @return the number of votes
     */
    public int getVotes() {
        return votes;
    }

    /**
     * Sets the number of seats assigned to this candidate.
     *
     * @param seats the number of seats to set
     */
    public void setSeats(int seats){
        this.seats = seats;
    }

    /**
     * Returns the total number of seats allocated to this candidate.
     *
     * @return the number of seats
     */
    public int getSeats(){
        return this.seats;
    }

    /**
     * Adds a seat to the candidate's total seat count.
     */
    public void addSeats(){
        this.seats += 1;
    }
}
