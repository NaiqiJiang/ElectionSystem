/**
 * Implements a Multi-Party Open-Mixed Voting (MPOMV) election system.
 * <p>
 * This class is designed to handle the complexities of an MPOMV election where each vote is cast directly
 * to candidates rather than parties. The MPOMV class extends the generic Election class and is tailored to
 * manage candidate-specific voting data, calculate total votes, and determine seat allocation based on individual
 * candidate performances. Key functionalities include loading ballot data, tallying votes for each candidate,
 * calculating and allocating seats based on votes, and providing detailed outputs of the election results.
 * This system allows for direct voter impact on candidate selection in a mixed voting environment.
 * <p>
 * Author: Naiqi Jiang, Ruirui Xu, Jiahao Sun
 */
package Election;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a Multi-Party Open-Mixed Voting (MPOMV) election system.
 * This class extends {@link Election} and handles the process of voting where
 * each vote directly impacts the candidates rather than parties. It calculates
 * the allocation of seats based on the total votes each candidate receives.
 */
public class MPOMV extends Election {
    private List<Candidate> candidates = new ArrayList<>();

    /**
     * Constructs an MPOMV election with specified total seats and votes, and initializes the candidate list.
     * @param totalSeats Total number of seats available in the election.
     * @param totalVotes Total number of votes cast in the election.
     * @param candidates Initial list of candidates participating in the election.
     * @throws IllegalArgumentException if the total number of seats is less than or equal to zero.
     */
    public MPOMV(int totalSeats, int totalVotes, List<Candidate> candidates) {
        super(totalSeats, totalVotes);
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than 0");
        }
        this.candidates = candidates;
    }

    /**
     * Conducts the election based on the provided ballot data.
     * This method processes each ballot, tallies votes for candidates, and then calculates seat allocations.
     * @param ballotData List of strings where each string represents votes in a single ballot.
     */
    @Override
    public void conductElection(List<String> ballotData) {
        if (this.totalVotes == 0) {
            System.out.println("No votes");
            return;
        }
    
        System.out.println("All ballots data:");
        ballotData.forEach(System.out::println);

        for (String ballotLine : ballotData) {
            System.out.println("candidate size " + candidates.size());
            String[] votes = ballotLine.split(",");
            for (int i = 0; i < votes.length; i++) {
                if ("1".equals(votes[i].trim()) && i < candidates.size()) {
                    candidates.get(i).addVote();
                    System.out.printf("Candidate %s received a vote\n", candidates.get(i).getName());
                }
            }
        }
        calculateSeats();
        printCandidateVotes();
    }

    /**
     * Prints the vote counts for each candidate.
     */
    private void printCandidateVotes() {
        System.out.println("Vote counts per candidate:");
        candidates.forEach(candidate -> 
            System.out.println("Candidate: " + candidate.getName() + ", Votes: " + candidate.getVotes()));
    }

    /**
     * Calculates seats for each candidate based on votes received.
     * Sorts candidates by votes in descending order and allocates seats to the top candidates based on available seats.
     */
    @Override
    public void calculateSeats() {
        candidates.sort(Comparator.comparing(Candidate::getVotes).reversed());
        for (int i = 0; i < Math.min(candidates.size(), this.totalSeats); i++) {
            candidates.get(i).addSeats();
        }
    }

    /**
     * Adds a candidate to the election.
     * @param candidate The candidate to add.
     */
    public void addCandidate(Candidate candidate) {
        this.candidates.add(candidate);
    }

    /**
     * Returns the list of candidates in the election.
     * @return List of candidates.
     */
    public List<Candidate> getCandidates() {
        return candidates;
    }
}
