/**
 * Defines the abstract structure for an election system.
 * <p>
 * This class serves as a base for specific types of elections, providing common properties
 * such as a list of parties, total seats, and total votes. It defines the abstract structure for
 * conducting an election and calculating seats, with specific implementation details left to subclasses.
 * This abstraction facilitates the application of different election mechanisms based on the same fundamental principles.
 * <p>
 * Author: Naiqi Jiang, Ruirui Xu, Jiahao Sun
 */
package Election;

import java.util.ArrayList;
import java.util.List;

public abstract class Election {
    /**
     * List of parties participating in the election.
     */
    protected List<Party> parties;

    /**
     * Total number of seats available in the election.
     */
    protected int totalSeats;

    /**
     * Total number of votes cast in the election.
     */
    protected int totalVotes;

    private String electionType; // Type of Election

    /**
     * Constructs an Election with the specified total seats and total votes.
     * This is the base constructor used by all types of election systems.
     *
     * @param totalSeats the total number of seats available in the election
     * @param totalVotes the total number of votes cast in the election
     */
    public Election(int totalSeats, int totalVotes) {
        this.parties = new ArrayList<>();
        this.totalSeats = totalSeats;
        this.totalVotes = totalVotes;
    }

    /**
     * Calculates the quota needed for a party to win a seat.
     * This static method provides a generic formula to calculate the number of votes needed per seat.
     *
     * @param totalVotes the total number of votes cast in the election
     * @param totalSeats the total number of seats available in the election
     * @return the quota required to win a seat
     * @throws IllegalArgumentException if the total number of seats is 0
     */
    public static double calculateQuota(int totalVotes, int totalSeats) {
        if (totalSeats == 0) {
            throw new IllegalArgumentException("Total number of seats cannot be zero.");
        }
        return (double) totalVotes / totalSeats;
    }

    /**
     * Adds a party to the list of parties participating in the election.
     * This method allows for dynamic addition of parties before the election is conducted.
     *
     * @param party the party to be added
     */
    public void addParty(Party party) {
        if (party != null) {
            this.parties.add(party);
        }
    }

    /**
     * Conducts the election based on the given ballot data.
     * This abstract method must be implemented by subclasses to specify the election process.
     *
     * @param ballotData a list of strings representing the ballot data
     */
    public abstract void conductElection(List<String> ballotData);

    /**
     * Calculates the distribution of seats among parties.
     * This abstract method must be implemented by subclasses to specify the seat allocation process.
     */
    protected abstract void calculateSeats();

    /**
     * Gets the list of parties participating in the election.
     *
     * @return the list of parties
     */
    public List<Party> getParties() {
        return parties;
    }

    /**
     * Finds a party by name among the list of parties.
     * This utility method helps locate a party based on its name, ignoring case differences.
     *
     * @param partyName the name of the party to find
     * @return the party if found, or null if no party matches the given name
     */
    public Party findPartyByName(String partyName) {
        for (Party party : this.parties) {
            if (party.getName().equalsIgnoreCase(partyName)) {
                return party;
            }
        }
        return null; // Returns null if no matching party is found
    }

    /**
     * Gets the total number of seats available in the election.
     *
     * @return the total number of seats
     */
    public int getTotalSeats() {
        return totalSeats;
    }

    /**
     * Gets the total number of votes cast in the election.
     *
     * @return the total number of votes
     */
    public int getTotalVotes() {
        return totalVotes;
    }

    /**
     * Updates the election with additional seats and votes.
     * This method can be used to dynamically adjust the election parameters.
     *
     * @param additionalSeats the number of additional seats available from new data
     * @param additionalVotes the number of additional votes cast from new data
     */
    public void updateElection(int additionalSeats, int additionalVotes) {
        this.totalSeats += additionalSeats;
        this.totalVotes += additionalVotes;
    }

    /**
     * Sets the type of the election.
     * <p>
     * This method allows changing the type of the election. The type can be any string
     * that the system recognizes as a valid election type.
     *
     * @param type The new type of the election as a String.
     */
    public void setElectionType(String type) {
        this.electionType = type;
    }

    /**
     * Retrieves the type of the election.
     * <p>
     * This method returns the current type of the election. The returned type is a
     * string that has been set previously by the {@link #setElectionType(String)} method.
     *
     * @return The current type of the election as a String.
     */
    public String getElectionType() {
        return this.electionType;
    }
}
