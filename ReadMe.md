Election System Simulation
Overview
This Java application simulates different types of elections including Closed Party List (CPL), Open Party List (OPL), Mixed Member Proportional (MPO), and Majoritarian Voting (MV). It is designed to manage the election process, calculate results based on vote counts, and assign seats to parties and candidates accordingly. The system uses .csv files to import voting data, simulating various scenarios for each election type.

Project Structure
The project is divided into two main sections: coding and testing. The coding section processes election data and outputs results, while the testing section verifies the logic and accuracy of the computations.

Data Input: The system reads from three .csv files for CPL testing and four for OPL testing. Each file represents a unique case, allowing the simulation to cover a wide range of election scenarios.
Results Output: All results are exported to an audit.txt file, which includes detailed information on the number of votes received by each party and candidate, the seats won, and the corresponding percentages. This setup ensures transparency and ease of verification of the election outcomes.
Command Line Interface: During the execution, print statements provide real-time feedback and insights into the ongoing processes, enhancing the visibility of the simulation's internal workings.

Running the Simulation
To run the simulation, simply execute the program in a Java-enabled IDE or command line setup by clicking "run without debugging." Make sure that the audit.txt file is present in the project directory as it is essential for importing and exporting data.

Conclusion
This simulation offers a robust platform for understanding and analyzing the mechanics of various election systems. By providing detailed data outputs and a dynamic testing environment, it serves as a valuable tool for educational purposes and in-depth electoral analysis.