/**
 * Manages the overall election process including loading ballot data, conducting elections,
 * finalizing results, and exporting audit files.
 * <p>
 * This class is responsible for orchestrating the steps of an election from data input to result output.
 * It handles various election types through conditional logic and method dispatching based on the specific
 * requirements of each election format. It provides methods to load ballot data from files, manage election
 * logic, compute final results, and generate detailed audit reports.
 * <p>
 * Author: Naiqi Jiang, Ruirui Xu, Jiahao Sun
 */
package Election;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * Manages elections by handling ballot data loading, result calculation, and audit report generation.
 */
public class ElectionManager {
    private Election election; // The election instance being managed
    private Party finalWinner = null; // The final winner of the election, determined after all calculations
    private int totalVotes; // Total votes accumulated from all files
    private int totalSeats; // Total seats accumulated from all files
    private Set<String> uniqueParties = new HashSet<>(); // To keep track of unique parties across all files
    List<String> ballots = new ArrayList<>(); 
    List<Candidate> candidates = new ArrayList<>();

    /**
     * Constructs an ElectionManager with a specific election.
     *
     * @param election The election to manage.
     */
    public ElectionManager(Election election) {
        this.election = election;
    }

    /**
     * Returns the current election instance being managed.
     *
     * @return The election instance.
     */
    public Election getElection() {
        return this.election;
    }

    /**
     * Loads ballot data from a specified file and conducts the election based on the data format specified within the file.
     * It supports multiple election types by distinguishing between them using the initial headers in the files.
     *
     * @param filename The path to the file containing ballot data.
     */
    public void loadBallotData(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            String electionType = scanner.nextLine();
            if("OPL".equals(electionType) || "CPL".equals(electionType)){
            int seats = Integer.parseInt(scanner.nextLine().trim());
            int votes = Integer.parseInt(scanner.nextLine());
    
            if (this.election == null) {
                // Only create a new Election instance if one does not already exist
                if ("CPL".equals(electionType)) {
                    this.election = new ClosedListElection(seats, votes);
                } else if ("OPL".equals(electionType)) {
                    this.election = new OpenListElection(seats, votes);
                } else {
                    throw new IllegalArgumentException("Invalid election type: " + electionType);
                }
            } else {
                // If an Election instance exists, update its seats and votes
                this.totalSeats += seats;
                this.totalVotes += votes;
                this.election.updateElection(seats, votes);
            }
            if ("OPL".equals(electionType)) { // OPL data process
                int numCandidates = Integer.parseInt(scanner.nextLine());
                loadOPLData(scanner, numCandidates, ballots);
            }   else if("CPL".equals(electionType)){ // CPL data process
                int numCandidates = Integer.parseInt(scanner.nextLine());
                loadCPLData(scanner, numCandidates, ballots);
            }   
            // MPO and MV data process
        }   else if ("MPO".equals(electionType) || "MV".equals(electionType)) {
            loadMPOMVData(scanner);
            }
            this.election.setElectionType(electionType);
            System.out.println("Election Type " + this.election.getElectionType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads Multi-Party Open-Mixed Voting (MPOMV) data from a scanner and processes election information.
     * <p>
     * This method reads the number of seats and candidates from the provided scanner, creates or updates
     * a mapping for candidates based on their names, and accumulates votes for each candidate. It handles
     * the instantiation or updating of an election instance based on the data read.
     * <p>
     * The method first reads the total number of seats and candidates from the input, followed by detailed
     * candidate information which it uses to either add new candidates to a global list or map existing candidates
     * to their indices in the list. It then reads votes, updates vote counts for each candidate accordingly,
     * and ensures the election instance is properly updated or created with the latest information.
     *
     * @param scanner A scanner object to read the data from the input source.
     */
    private void loadMPOMVData(Scanner scanner) {
        int seats = Integer.parseInt(scanner.nextLine().trim());  // Read the number of seats available.
        int numCandidates = Integer.parseInt(scanner.nextLine().trim());  // Read the number of candidates.

        // Create a candidate index mapping for the current file
        Map<String, Integer> currentFileCandidateIndex = new HashMap<>();
        String candidatesLine = scanner.nextLine().trim();  // Read the line containing candidate info.
        String[] candidateInfos = candidatesLine.split("\\], \\[");  // Split candidate information.

        // Process each candidate information block
        for (int i = 0; i < candidateInfos.length; i++) {
            String candidateInfo = candidateInfos[i].replace("[", "").replace("]", "");  // Clean up candidate info string.
            String name = candidateInfo.split(", ")[0].trim();  // Extract candidate name.

            int globalIndex = -1;
            // Check if the candidate already exists in the global list
            for (int j = 0; j < candidates.size(); j++) {
                if (candidates.get(j).getName().equals(name)) {
                    globalIndex = j;  // Set global index if candidate exists.
                    break;
                }
            }
            // If the candidate does not exist, add them to the global list.
            if (globalIndex == -1) {
                Candidate newCandidate = new Candidate(name);
                candidates.add(newCandidate);
                globalIndex = candidates.size() - 1;
            }
            // Update the current file's candidate index mapping.
            currentFileCandidateIndex.put(name, globalIndex);
        }

        // Accumulate votes from remaining lines.
        int totalVotes = Integer.parseInt(scanner.nextLine().trim());  // Read the total votes.
        while (scanner.hasNextLine()) {
            String votesLine = scanner.nextLine().trim();
            if (!votesLine.isEmpty()) {
                String[] votes = votesLine.split(",");
                for (int i = 0; i < votes.length; i++) {
                    if ("1".equals(votes[i].trim()) && i < candidateInfos.length) {
                        String name = candidateInfos[i].replace("[", "").replace("]", "").split(", ")[0].trim();
                        if (currentFileCandidateIndex.containsKey(name)) {
                            int globalIndex = currentFileCandidateIndex.get(name);
                            candidates.get(globalIndex).addVote();  // Add vote to the candidate.
                        }
                    }
                }
            }
        }

        // Create or update election instance
        if (this.election == null || !(this.election instanceof MPOMV)) {
            this.election = new MPOMV(seats, totalVotes, candidates);  // Create a new MPOMV election if one does not exist.
        } else {
            this.election.updateElection(seats, totalVotes);  // Update existing election with new data.
        }
    }

    
    /**
     * Loads Closed Party List (CPL) data from the scanner, sets up party and candidate information, and adds ballots.
     * This method reads the details of each party and its candidates from the input, then loads ballots from subsequent lines.
     *
     * @param scanner    Scanner instance for reading input. Assumed to be opened and ready.
     * @param numParties Number of parties listed in the file, which dictates the number of times the loop will run.
     * @param ballots    List of ballots to be populated. Each ballot is a string representing votes cast in the election.
     */
    private void loadCPLData(Scanner scanner, int numParties, List<String> ballots) {
        for (int i = 0; i < numParties; i++) {
            // Read party information from the scanner and split by commas to parse individual details.
            String[] partyInfo = scanner.nextLine().split(",");
            String partyName = partyInfo[0].trim();
            Party party = findPartyByName(partyName);  // Attempt to find an existing party by name.

            if (party == null) {
                party = new Party(partyName);  // Create a new party if it doesn't exist.
                this.election.addParty(party);  // Add the newly created party to the election.
                uniqueParties.add(partyName);  // Track unique parties to prevent duplicates.
            }

            // Process each candidate associated with the party.
            for (int j = 1; j < partyInfo.length; j++) {
                String candidateName = partyInfo[j].trim();
                if (!party.hasCandidate(candidateName)) {
                    Candidate candidate = new Candidate(candidateName, party);  // Create a new candidate linked to this party.
                    party.addCandidate(candidate);  // Add the candidate to the party.
                }
            }
        }

        // Read the ballot data, which follows the party and candidate information.
        while (scanner.hasNextLine()) {
            String ballotLine = scanner.nextLine().trim();
            if (!ballotLine.isEmpty()) {
                ballots.add(ballotLine);  // Add each non-empty ballot to the list.
            }
        }
    }

    /**
     * Searches for a party by its name within the current election's list of parties.
     * This helper function supports the primary data-loading method by providing quick access to party objects.
     *
     * @param partyName The name of the party to find.
     * @return The found Party object or null if no party matches the given name.
     */
    private Party findPartyByName(String partyName) {
        for (Party party : this.election.getParties()) {  // Iterate over all parties in the current election.
            if (party.getName().equals(partyName)) {  // Check if the party's name matches the given name.
                return party;  // Return the matching party.
            }
        }
        return null;  // Return null if no matching party is found.
    }

    

    /**
     * Loads Open Party List (OPL) data from the scanner, setting up party and candidate information, and accumulates ballots.
     * This method handles the initial setup by reading each party and their respective candidates from the input,
     * and then processes each vote cast in subsequent lines to accumulate votes for candidates.
     *
     * @param scanner Scanner instance for reading input.
     * @param numCandidates The number of candidates expected to be listed in the data.
     * @param ballots A list where ballots will be accumulated, representing each vote.
     */
    private void loadOPLData(Scanner scanner, int numCandidates, List<String> ballots) {
        Map<String, Candidate> candidatesByName = new HashMap<>();  // Map to track candidates by name for quick lookup.

        // Populate the map with candidates from existing parties
        for (Party party : this.election.getParties()) {
            for (Candidate candidate : party.getCandidates()) {
                candidatesByName.put(candidate.getName(), candidate);
            }
        }
    
        // Read candidate details and ensure all candidates are properly initialized within their respective parties
        for (int i = 0; i < numCandidates; i++) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;  // Skip empty lines
            String[] candidateInfo = line.split(",");  // Split party name and candidate name
            String partyName = candidateInfo[0].trim();
            String candidateName = candidateInfo[1].trim();
    
            // Find or create the party
            Party party = this.election.findPartyByName(partyName);
            if (party == null) {
                party = new Party(partyName);  // Create a new party if it doesn't exist
                this.election.addParty(party);
                uniqueParties.add(partyName);  // Track unique parties to avoid duplicates
            }
    
            // Find or create the candidate within the party
            Candidate candidate = candidatesByName.get(candidateName);
            if (candidate == null || !candidate.getParty().getName().equals(partyName)) {
                candidate = new Candidate(candidateName, party);
                party.addCandidate(candidate);
                candidatesByName.put(candidateName, candidate);  // Update the map with the new or updated candidate
            }
        }
    
        // Process the ballot data, which follows the candidate information
        while (scanner.hasNextLine()) {
            String votesLine = scanner.nextLine().trim();
            if (votesLine.isEmpty()) continue;
            String[] votes = votesLine.split(",");
            for (int i = 0; i < votes.length; i++) {
                if ("1".equals(votes[i].trim())) {
                    // Convert index in line to candidate object
                    Candidate candidate = findCandidateByIndex(i, candidatesByName);
                    if (candidate != null) {
                        candidate.addVote();  // Increment vote count for the candidate
                        candidate.getParty().addVote();  // Increment vote count for the candidate's party
                    }
                }
            }
        }
    }
    
    /**
     * Finds a candidate by the index specified in the ballot line. The index correlates to the order in which candidates
     * were listed during data setup.
     *
     * @param index The index in the ballot line.
     * @param candidatesByName A map containing candidates keyed by their names for quick access.
     * @return The Candidate if found, null otherwise.
     */
    private Candidate findCandidateByIndex(int index, Map<String, Candidate> candidatesByName) {
        int currentIndex = 0;  // Counter to match index with candidate position in the list
        for (Candidate candidate : candidatesByName.values()) {
            if (currentIndex == index) {
                return candidate;  // Return the candidate corresponding to the index
            }
            currentIndex++;
        }
        return null;  // Return null if no matching candidate is found at that index
    }


    /**
     * Finalizes the election results by determining the winner based on the number of seats won.
     * This method manages potential tie scenarios by employing a tie-breaking mechanism.
     */
    public void finalizeElectionResults() {
        // Check if there is a tie vote between parties needed to decide the final winner
        List<Party> tiedPartiesForSeats = findTiedPartiesForSeats();
    
        // If there is a tie vote, solve it by random method
        if (tiedPartiesForSeats.size() > 1) {
            System.out.println("In the event of a tie for seats, a coin toss is required to determine the final winner.");
            finalWinner = coinToss(tiedPartiesForSeats);
            System.out.println("The final winner determined by a coin toss is: " + finalWinner.getName());
        } else if (tiedPartiesForSeats.size() == 1) {
            // If only one party gets the most seats, that party becomes the winner
            finalWinner = tiedPartiesForSeats.get(0);
        } else {
            // If there is no tie vote, find the party with the most seats as the winner
            Party winner = null;
            int maxSeats = -1;
            for (Party party : election.getParties()) {
                if (party.getSeats() > maxSeats) {
                    winner = party;
                    maxSeats = party.getSeats();
                }
            }
            finalWinner = winner;
        }
    }
    

    /**
     * Identifies parties that are tied for having the highest number of seats after an election.
     * <p>
     * This method scans through all parties involved in the election and determines which ones have the highest number
     * of seats. If multiple parties have the same highest number of seats, all such parties are considered tied.
     * <p>
     * This is particularly useful in situations where a tie-breaking mechanism needs to be employed to determine a final
     * winner or for further processing in the electoral process.
     *
     * @return A list of parties that are tied for the highest number of seats.
     */
    private List<Party> findTiedPartiesForSeats() {
        int maxSeats = -1;
        List<Party> tiedParties = new ArrayList<>(); // List to hold parties tied for the maximum number of seats.

        // First, determine the maximum number of seats that any party has achieved.
        for (Party party : election.getParties()) {
            if (party.getSeats() > maxSeats) {
                maxSeats = party.getSeats(); // Update maxSeats if a party has more seats than the current max.
            }
        }

        // Collect all parties that have the maximum number of seats found in the previous step.
        for (Party party : election.getParties()) {
            if (party.getSeats() == maxSeats) { // Check if the party's seats match the maximum seats.
                tiedParties.add(party); // Add the party to the list of tied parties.
            }
        }

        return tiedParties; // Return the list of parties that are tied for having the most seats.
    }



    /**
     * Conducts a coin toss among tied parties to determine a winner.
     *
     * @param tiedParties A list of parties tied for seats.
     * @return The winning party determined by a random coin toss.
     */
    private Party coinToss(List<Party> tiedParties) {
        Random random = new Random();
        // Initialize win counts for each party
        int[] winCounts = new int[tiedParties.size()];
    
        // Conduct 1001 coin tosses
        for (int i = 0; i < 1001; i++) {
            int tossIndex = random.nextInt(tiedParties.size());
            winCounts[tossIndex]++;
        }
    
        // Determine which party won the most tosses
        int maxWinsIndex = 0;
        for (int i = 1; i < winCounts.length; i++) {
            if (winCounts[i] > winCounts[maxWinsIndex]) {
                maxWinsIndex = i;
            }
        }
    
        // Return the party that won the most tosses
        return tiedParties.get(maxWinsIndex);
    }
    
    /**
     * Exports a detailed audit file with the election results, including candidate votes and seat allocations.
     * This method writes to a file specified by the filename parameter.
     *
     * @param filename The name of the file to which the audit report is to be written.
     */
    public void exportAuditFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            String electionType = this.election.getElectionType(); // 获取选举类型
    
            // Election type
            if ("MPO".equals(electionType)) {
                writer.println("Type of Election: MPO");
            } else if ("MV".equals(electionType)) {
                writer.println("Type of Election: MV");
            } else {
                writer.println("Type of Election: " + ("CPL".equals(electionType) ? "CPL" : "OPL"));
            }
    
            // General information
            writer.println("Number of Seats: " + this.election.getTotalSeats());
            writer.println("Number of Ballots: " + this.election.getTotalVotes());
    
            // Only print number of parties if it's not MPO or MV
            if (!"MPO".equals(electionType) && !"MV".equals(electionType)) {
                writer.println("Number of Parties: " + uniqueParties.size());
    
                // Candidate and party information
                writer.println("Candidates and Party Affiliation:");
                for (Party party : election.getParties()) {
                    writer.println("Party: " + party.getName());
                    for (Candidate candidate : party.getCandidates()) {
                        writer.println("  Candidate: " + candidate.getName() + ", Votes: " + candidate.getVotes());
                    }
                }
    
                // Calculate and print seats allocated using large balance method
                writer.println("-----------------------------------------------------------------------------------------------");
                writer.printf("%-15s %-10s %-15s %-10s %-15s %-10s %-10s\n",
                        "Parties", "Votes", "First Allocation", "Remaining", "Second Allocation", "Final", "% of Vote");
                writer.println("-----------------------------------------------------------------------------------------------");
    
                for (Party party : election.getParties()) {
                    int finalSeats = party.getInitialSeats() + party.getSecondAllocationSeats();
                    double percentOfVotes = (double) party.getVotes() / election.getTotalVotes() * 100;
                    writer.printf("%-15s %-10d %-15d %-10d %-15d %-10d %-10.1f%%\n",
                            party.getName(), party.getVotes(), party.getInitialSeats(), party.getRemainderVotes(), party.getSecondAllocationSeats(),
                            finalSeats, percentOfVotes);
                }
                writer.println("-----------------------------------------------------------------------------------------------");
            }
    
            // Winner list
            writer.println("List of Seat Winners and their Party Affiliation:");
            for (Party party : election.getParties()) {
                for (Candidate candidate : party.getCandidates()) {
                    if (candidate.hasSeats()) {
                        writer.println("Candidate: " + candidate.getName() + ("MPO".equals(electionType) || "MV".equals(electionType) ? "" : ", Party: " + party.getName()));
                    }
                }
            }
    
            // MPO and MV specific information
            if ("MPO".equals(electionType) || "MV".equals(electionType)) {
                int total = 0;
                for (Candidate candidate : candidates) {
                    total += candidate.getVotes();
                }
                writer.println("-----------------------------------------------------------------------------------------------");
                writer.printf("%-15s %-10s %-15s %-10s\n", "Candidate", "Votes", "Seats", "Percentage");
                writer.println("-----------------------------------------------------------------------------------------------");
                for (Candidate candidate : candidates) {
                    double percentage = ((double) candidate.getVotes() / total) * 100;  // Calculate vote percentage
                    writer.printf("%-15s %-10d %-15d %.2f%%\n",
                            candidate.getName(),
                            candidate.getVotes(),
                            candidate.getSeats(),
                            percentage);
                }
                writer.println("-----------------------------------------------------------------------------------------------");
            }
    
        } catch (IOException e) {
            System.err.println("An error occurred during writing to the file: " + filename);
            e.printStackTrace();
        }
    }
    
    
    /**
     * Conducts the election based on accumulated ballots. This method is typically called after all ballot data
     * has been loaded and is ready to be processed.
     */
    public void conductElection() {
        if (this.election != null) {
            this.election.conductElection(this.ballots);
        }
    }
    
    /**
     * The entry point of the election management application.
     * <p>
     * This method allows running the election process from the command line. It expects an optional
     * filename argument that specifies the location of the ballot data file. If no argument is provided,
     * it prompts the user to enter the filename. It then loads the ballot data, conducts the election,
     * finalizes the results, and exports an audit file.
     *
     * @param args the command-line arguments, where the first argument can be the filename of the ballot data file.
     */
    public static void main(String[] args) {
        ElectionManager manager = new ElectionManager(null); // Initialize without a specific election
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file names separated by space, and type 'Done' when you are finished:");
        List<String> filenames = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if ("Done".equalsIgnoreCase(input.trim())) {
                break;
            }
            filenames.addAll(Arrays.asList(input.split("\\s+")));
        }
        scanner.close();

        for (String filename : filenames) {
            if (!new File(filename).exists()) {
                System.err.println("Error: The file " + filename + " does not exist.");
                continue;
            }
            manager.loadBallotData(filename); // Accumulate data from each file
        }
        manager.conductElection(); // Process the accumulated ballots
        // manager.printPartyDetails();
        manager.finalizeElectionResults(); // Finalize and summarize results after all files are processed
        manager.exportAuditFile("audit.txt"); // Output results to a single audit file
    }

}