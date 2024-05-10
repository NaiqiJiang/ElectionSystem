/**
 * ClosedListElection.java
 * 
 * This Java file contains the ClosedListElection class, which extends the Election class to represent a closed list election system.
 * In this system, voters cast their votes for parties rather than individual candidates, and seats are allocated to parties based
 * on the total number of votes each party receives. The class includes methods for conducting the election, calculating seats based
 * on votes, and allocating any remaining seats among parties. This implementation showcases the application of inheritance,
 * encapsulation, and polymorphism principles in Java.
 * 
 * Author: [Naiqi Jiang, Ruirui Xu, Jiahao Sun]
 */
package Election;


 import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
 /**
  * Represents a closed list election system.
  * <p>
  * In this system, voters cast their votes for parties rather than individual candidates.
  * Seats are allocated to parties based on the total number of votes each party receives.
  */
 public class ClosedListElection extends Election {
 
     /**
      * Constructs a new ClosedListElection with the specified number of total seats and total votes.
      * 
      * @param totalSeats the total number of seats available in the election
      * @param totalVotes the total number of votes cast in the election
      * @throws IllegalArgumentException if the total number of seats is less than or equal to 0
      */
     public ClosedListElection(int totalSeats, int totalVotes) {
         super(totalSeats, totalVotes);
         if (totalSeats <= 0) {
             throw new IllegalArgumentException("Total seats must be greater than 0");
         }
         if(totalVotes < 0){
            throw new IllegalArgumentException("Total Votes must be greater than 0");
         }
     }
 
     /**
      * Conducts the election based on the provided ballot data.
      * <p>
      * This method processes each vote in the ballot data, updates the vote counts for each party,
      * and then calculates the seats allocated to each party.
      * 
      * @param ballotData a list of strings representing the votes for each party
      */
     @Override
     public void conductElection(List<String> ballotData) {
         if (this.totalVotes == 0) {
             // If there are no votes, no seats will be allocated
             return;
         }
         // Process each row of voting data
         for (String ballotLine : ballotData) {
             String[] votes = ballotLine.split(",");
             for (int i = 0; i < votes.length; i++) {
                 if ("1".equals(votes[i].trim())) {
                     Party party = this.parties.get(i);
                     party.addVote();
                     break;
                 }
             }
         }
         calculateSeats();
     }
 
    /**
     * Calculates and allocates seats to each party based on the total votes received.
     * <p>
     * This method uses a quota system to determine the initial number of seats each party receives. 
     * The quota is calculated based on the total votes divided by the total seats available. Each party's seats 
     * are initially allocated by dividing the party's total votes by this quota. If a party's votes are less than 
     * the quota, it does not receive any initial seats. After the initial allocation, the method handles the 
     * distribution of any remaining seats.
     */
    @Override
    public void calculateSeats() {
        double quota = calculateQuota(this.totalVotes, this.totalSeats);  // Calculate the number of votes needed per seat.
        System.out.println("Calculating seats based on the voting quota");

        for (Party party : this.parties) {
            System.out.println("party " + party.getName() +" votes " + party.getVotes());
            int initialSeats = (int) (party.getVotes() / quota);  // Calculate initial seats based on the quota.
            System.out.println("initial seats " + initialSeats);

            if(party.getVotes() < quota) {  // Ensure a party must reach the quota to receive any seats.
                initialSeats = 0;
            }
            party.setInitialSeats(initialSeats);  // Set the number of initial seats for the party.
            party.setRemainderVotes((int)(party.getVotes() - (initialSeats * quota)));  // Calculate and set the remainder votes after seat allocation.

            // Print detailed allocation results for each party.
            System.out.printf("Party: %s, Votes: %d, Initial Seats: %d, Remainder Votes: %d\n",
                              party.getName(), party.getVotes(), initialSeats, party.getRemainderVotes());
        }

        allocateRemainingSeats();  // Call a method to allocate any remaining seats.
    }

    
     
    /**
     * Allocates any remaining seats to parties based on their number of remainder votes.
     * <p>
     * This method is called after the initial allocation of seats based on the quota. It further distributes
     * any remaining seats among the parties, giving priority to those with the highest number of remainder votes.
     * In the event of a tie in remainder votes, a random selection is made to determine which party receives the seat.
     * This helps ensure fairness in seat allocation when the vote counts do not neatly divide into the total seats.
     */
    private void allocateRemainingSeats() {
        int totalInitialSeats = this.parties.stream().mapToInt(Party::getInitialSeats).sum();  // Sum up all initially allocated seats.
        int remainingSeats = this.totalSeats - totalInitialSeats;  // Calculate the number of seats still to be allocated.
        Random random = new Random();
        System.out.println("Remaining seats: " + remainingSeats);

        while (remainingSeats > 0) {
            List<Party> eligibleParties = new ArrayList<>();
            int highestRemainder = -1;

            // Identify the highest remainder votes that have not yet resulted in a seat.
            for (Party party : this.parties) {
                if (!party.hasWonSeatInRound() && party.getRemainderVotes() > highestRemainder) {
                    highestRemainder = party.getRemainderVotes();
                }
            }

            // Collect all parties that have this highest remainder vote count and have not won a seat this round.
            for (Party party : this.parties) {
                if (party.getRemainderVotes() == highestRemainder && !party.hasWonSeatInRound()) {
                    eligibleParties.add(party);
                }
            }

            if (eligibleParties.isEmpty()) {
                // If no eligible parties are found, reset the round status for all parties and continue to the next iteration.
                this.parties.forEach(Party::resetWonSeatInRound);
                continue;
            }

            // Randomly select one of the eligible parties to receive a seat.
            Party selectedParty = eligibleParties.get(random.nextInt(eligibleParties.size()));
            selectedParty.addSecondAllocationSeat();
            selectedParty.setWonSeatInRound(true);  // Mark the party as having won a seat this round.

            remainingSeats--;  // Decrement the count of remaining seats.
        }

        // Allocate seats to the candidates within each party based on their vote counts.
        for (Party party : this.parties) {
            int seatsWon = party.getInitialSeats() + party.getSecondAllocationSeats();
            List<Candidate> candidates = party.getCandidates();
            // Sort candidates within each party by votes in descending order to prioritize higher vote-getters.
            candidates.sort((c1, c2) -> Integer.compare(c2.getVotes(), c1.getVotes()));

            // Allocate seats to candidates based on the number of seats their party has won.
            for (int i = 0; i < seatsWon && i < candidates.size(); i++) {
                candidates.get(i).allocateSeat();  // Assign a seat to each top candidate until all seats are filled.
            }
        }
    }

 }
 