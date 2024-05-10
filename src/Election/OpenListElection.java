/**
 * OpenListElection.java
 *
 * This class represents an open list election system where voters vote for individual candidates rather than parties.
 * The seats are allocated based on the total number of votes received by each candidate's party, taking into account the individual preferences for candidates.
 *
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */
package Election;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implements an election process using the open list system.
 */
public class OpenListElection extends Election {

    /**
     * Constructs an OpenListElection with specified total seats and total votes.
     *
     * @param totalSeats the total number of seats to be allocated
     * @param totalVotes the total number of votes cast in the election
     * @throws IllegalArgumentException if the total number of seats is less than or equal to 0
     */
    public OpenListElection(int totalSeats, int totalVotes) {
        super(totalSeats, totalVotes);
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than 0");
        }
        if(totalVotes < 0){
           throw new IllegalArgumentException("Total Votes must be greater than 0");
        }
    }
    
    /**
     * Conducts the election process based on the given ballot data.
     * This method processes each ballot, updates votes for candidates and their corresponding parties,
     * and calculates the seats distribution after all ballots are processed.
     *
     * @param ballotData a list of strings representing the votes for each candidate
     */
    @Override
    public void conductElection(List<String> ballotData) {
        // Process each row of voting data
        for (String ballotLine : ballotData) {
            if (ballotLine == null || ballotLine.trim().isEmpty()) {
                continue; // Skip empty ballots.
            }
            String[] votes = ballotLine.split(",");
            for (int i = 0; i < votes.length; i++) {
                if ("1".equals(votes[i].trim())) {
                    Candidate candidate = findCandidateByIndex(i);
                    if (candidate != null) {
                        candidate.addVote();
                        candidate.getParty().addVote();
                    }
                }
            }
        }
        calculateSeats();
    }
    
    
    /**
     * Finds the candidate corresponding to the given index in the list of all candidates across all parties.
     * This method helps map the index from the ballot to the actual candidate object.
     *
     * @param index the index of the candidate in the ballot
     * @return the Candidate if found, otherwise null
     */
    private Candidate findCandidateByIndex(int index) {
        // Traverse all parties and candidates and find the candidate with the corresponding index
        int currentIndex = 0;
        for (Party party : this.parties) {
            for (Candidate candidate : party.getCandidates()) {
                if (currentIndex == index) {
                    return candidate;
                }
                currentIndex++;
            }
        }
        return null; // If no matching candidate is found, return null
    }

    /**
     * Calculates and allocates seats to parties based on the votes they have received.
     * This method first determines the initial allocation of seats based on a quota,
     * and then distributes any remaining seats using a highest remainder method.
     */
    @Override
    protected void calculateSeats() {
        double quota = calculateQuota(this.totalVotes, this.totalSeats);  // Calculate the votes required per seat.
        System.out.println("Total Votes: " + this.totalVotes + ", Total Seats: " + this.totalSeats);
        System.out.printf("Quota (Votes per seat): %.3f\n", quota);
        System.out.println("Initial seat allocation based on quota:");

        for (Party party : this.parties) {
            System.out.println("party votes " + party.getVotes());
            int initialSeats = (int) (party.getVotes() / quota);  // Calculate initial seats based on the quota.
            System.out.println("initial seats " + initialSeats);
            if (party.getVotes() < quota) {  // Ensure that a party must meet at least one quota to receive seats.
                initialSeats = 0;
            }
            party.setInitialSeats(initialSeats);
            party.setRemainderVotes((int) (party.getVotes() - (initialSeats * quota)));  // Calculate remaining votes after seat allocation.

            System.out.printf("Party: %s, Votes: %d, Initial Seats: %d, Remainder Votes: %d\n",
                              party.getName(), party.getVotes(), initialSeats, party.getRemainderVotes());
        }

        allocateRemainingSeats();  // Allocate remaining seats that were not assigned during the initial allocation.
        System.out.println("After allocating remaining seats:");
        for (Party party : this.parties) {
            System.out.printf("Party: %s, Total Seats after all allocations: %d\n",
                              party.getName(), party.getSeats());  // Print the total seats allocated to each party after all allocations.
        }
    }
    

    /**
     * Allocates any remaining seats to parties based on their number of remainder votes after initial allocation.
     * This method ensures that seats are distributed fairly among parties that have a high number of remainder votes.
     */
    private void allocateRemainingSeats() {
        int totalInitialSeats = this.parties.stream().mapToInt(Party::getInitialSeats).sum();
        int remainingSeats = this.totalSeats - totalInitialSeats;
        Random random = new Random();
        System.out.println("remaining seats: " + remainingSeats);
        while (remainingSeats > 0) {
            // Check if all parties have gained seats at least once
            boolean allPartiesWonAtLeastOnce = this.parties.stream().allMatch(Party::hasWonSeatInRound);
            
            // If so, resets the hasWonSeatInRound flag for all parties, allowing them to compete again in a new allocation round
            if (allPartiesWonAtLeastOnce) {
                this.parties.forEach(Party::resetWonSeatInRound);
            }
    
            List<Party> eligibleParties = new ArrayList<>();
            int highestRemainder = 0;
            
            // First, find the highest remainder votes among parties that haven't won a seat this round.
            for (Party party : this.parties) {
                if (!party.hasWonSeatInRound() && party.getRemainderVotes() > highestRemainder) {
                    highestRemainder = party.getRemainderVotes();
                }
            }
            
            // Then, add all parties with the highest remainder to the list of eligible parties.
            for (Party party : this.parties) {
                if (party.getRemainderVotes() == highestRemainder && !party.hasWonSeatInRound()) {
                    eligibleParties.add(party);
                }
            }
    
            boolean tie = eligibleParties.size() > 1 && eligibleParties.get(0).getRemainderVotes() == eligibleParties.get(1).getRemainderVotes();
    
            if (tie) {
                // Handle tie votes
                List<Party> tiedParties = eligibleParties.stream()
                    .filter(p -> p.getRemainderVotes() == eligibleParties.get(0).getRemainderVotes())
                    .collect(Collectors.toList());
                Party selectedParty = tiedParties.get(random.nextInt(tiedParties.size()));
                selectedParty.addSeats();
                selectedParty.addSecondAllocationSeat();
            } else {
                // If no tie votes, allocated to the party with the most remaining votes
                Party party = eligibleParties.get(0);
                party.addSecondAllocationSeat();
            }
            remainingSeats--;
        }
        
        for (Party party : this.parties) {
            List<Candidate> candidates = new ArrayList<>(party.getCandidates());
            // Sort candidates by votes in descending order
            candidates.sort((c1, c2) -> Integer.compare(c2.getVotes(), c1.getVotes()));
        
            // Use a temporary variable to track party distribution of seats
            int tempSeats = party.getInitialSeats() + party.getSecondAllocationSeats();
        
            // Allocate seats to candidates until all seats for that party are allocated
            for (Candidate candidate : candidates) {
                if (tempSeats > 0 && !candidate.hasSeats()) {
                    candidate.allocateSeat();  // Mark a candidate as having secured a seat
                    tempSeats--;  // Reduce temporary seat count            
                }
            }
        }
        
        System.out.println("All seats have been successfully allocated.");
    }
}
