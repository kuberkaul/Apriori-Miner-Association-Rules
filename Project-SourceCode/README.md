**********************************README*********************************

Files present:
1) "kk2872-proj3" zipped Project folder
2) README File in the main project folder
3) Example-run.txt in the main project folder
4) Makefile in the main project folder
5) Integrated Dataset CSV file in the "src" folder
6) "output.txt" will be created in "src" folder that will have large-item sets and high-confidence rules

File/Directory Listing
	README - This file.
	AprioriMiner.java - Source code for the project. Includes runnable main method and all code for the project.
	Makefile - Builds the project.
	bin - directory that contains the compiled .class files
	INTEGRATED-DATASET.csv - The dataset chosen to derive the high-confidence rules from.
	output.txt - Produces the output of large item sets and high-confidence rules.

How to Build the Program
	Extract all files in a directory.
	Follow instructions in Makefile.
	The bin directory should be populated with .class files after the "javac AprioriMiner.java" command.

How to Run the Program
	
	The way to run the program is to follow the instructions in the Makefile using the following syntax:
	
	1.  Complete command line:
		java AprioriMiner INTEGRATED-DATASET.csv <minSupport> <minConfidence> 
	
Where support is the support threshold from 0 to 1 and confidence is the coverage threshold. 

	

Design

	Our project is fully contained in the AprioriMiner.java class. The command line arguments are accepted and calls to various various methods are made. We have used a programming construct that is 	pretty simplistic without any complications to understand the code.
		
Description of Part 1 Design (Dataset and its Interestingness)

We have used the Graffiti Locations dataset currently available in the NYC open-data (https://data.cityofnewyork.us/browse?limitTo=datasets) at https://data.cityofnewyork.us/Other/Graffiti-Locations/2j99-6h29 to generate our INTEGRATED-DATASET.csv for deriving the high association rules from it. The dataset gives us the Addresses, current status, and coordinates of requests to clean graffiti (other than bridges or highways) received from the public and SCOUT in the last 12 months.

The only refinement we did to our dataset Graffiti Location is to refine it to out. We removed superfluous attributes/columns like "Date Started", "Date Ended", "Police Precinct", "City Council Diistrict", "X-Co-ordinate", "Y-Co-ordinate".

So, now the INTEGRATED DATASET contains only what is important and relevant columns to getting meaningful association rules and large item sets. We would apply Association mining Rules (Section 2.1 of the Agrawal and Srikant paper in VLDB 1999) to implement the Apriori Algorithm and determine the large itemsets of the graffiti locations and generate the high-confidence association rules, in decreasing order of confidence, reporting the support and confidence of each rule (e.g., [item1] => [item2] (Conf: 100%, Supp: 7.4626%)).

Through generating these confidence rules, we can determine which Boroughs have the maximum number of graffiti and what is the status of such cases currently. We also provide the resolution action performed for such cases. The interestingness reported is that there has been proved co-relation between graffiti/graffiti locations and the crime and littering correlation. This can be proved through various studies such as:

1. http://www.denverpost.com/news/ci_9929042 - Denver Daily newspaper reported the correlation between crime and the graffiti locations. The relation has been reported to be directly proportional.
2. http://www.livescience.com/7599-graffiti-triggers-crime-littering.html - Live Science reported a direct triggering of crime and littering in the locations where maximum number of graffiti locations are found.
3. http://www.spindrift.org/graffiti.pdf - A recent paper was written which researched into the direct correlation of crime and graffiti and proved that the control of graffiti art in locations actually reduced the crime by a percentage hence reinforcing the fact that there is a correlation between crime and graffiti. 

According to the example-run.txt file that we produced, some of the rules clearly infer that:

1. [Open, BROOKLYN] ==> [Site downloaded for cleaning] (Conf: 53.41880341880342%, Supp: 4.3047041807287005%) - The rule clearly describes that almost 50% times that the status of a graffiti location is open then it is in Brooklyn area. It also describes that in such a condition site is downloaded for cleaning.


2.  [Notice of Intent to Clean and Forever Graffiti Free Form sent, Open] ==> [BROOKLYN] (Conf: 23.30009066183137%, Supp: 1.7700943591156415%)- This rule indicates that whenever a notice to permanently clean and forever free the location from graffiti is sent and the status of the location is open i.e not resolved then the location is 23% times Brooklyn 


3.  [Open, BROOKLYN] ==> [Notice of Intent to Clean and Forever Graffiti Free Form sent] (Conf: 21.965811965811966%, Supp: 1.7700943591156415%) - The rule describes that almost 20 percent times that the status of the graffiti resolution is open i.e. not resolved and it is found in Brooklyn then a notice of intent to clean and forever free the graffiti form is sent to the location because of the high infestation.


4. [Open] ==> [BRONX] (Conf: 25.072690673227466%, Supp: 7.720917418554997%)- The rule clearly describes that almost 26% times that the status of a graffiti location is open then it is in Bronx area. 

Hence most of the graffiti locations with status open i.e. not yet resolved are found in Brooklyn and Bronx and the most number of Forever Graffiti Free Form are sent there.


Now to finally prove the correctness of the rules that we inferred, we did research into the crime rates/homicide rates in New York and found that our rules were strongly supported by the official crime rates given by:

1. NY Times - http://projects.nytimes.com/crime/homicides/map
The study clearly states that the maximum numbers of homicides are reported in Brooklyn and Bronx for New York area and this has been clearly inferred from the rules that we derived from the graffiti locations.

2. SpotCrime - http://www.spotcrime.com/ny/new%20york
The live site gives the most recent statistics of where a crime of any nature is taking place in new York and through the statistics page of the site it can be clearly inferred that the maximum number of crimes take place mostly in the Bronx, Brooklyn boroughs.

Conclusion - The conclusion that we arrive to through our rules is that:

a. Our rules have a direct correlation to the crime spot hotspots present in the New York and therefore we report that crime can be greatly controlled by controlling the graffiti art in the graffiti hotspot locations.
b. Our rules also report which places have graffiti locations having open status and we hypothesize that these locations are the ones with heavy crime rates and therefore should be immediately looked upon for security purpose.
c. Therefore, we are providing concrete proof that crime is directly proportional to the graffiti locations with the New York being the center point of example.


NOTE: The dataset has very varying values for the graffiti locations and there is large number of such values, the program shows the most interesting output for support between .07 and .1 which has been reported in example-run.txt.


Internal Design of the Project(Part-2):

We are using the Original implementation of Apriori Algorithm for generating association rules as specified in paper (Fast Algorithms for Mining Association Rules by Agarwal, Srikant). However, some additional pruning and modification is also implemented in order to get rid of some unnecessary transitive association rules between multi-dimensional large-item sets. Also, cycles of such association rules are also removed along with transitive rules.

The project primarily comprises of the following class files:
1. largeItemSetList: This is the primary data structure class where we store the large item sets found in form of a list of set of set of strings created for each transaction. The setters and get functions are specified. This complex data structure is used so as to simplify the implementation of the Apriori algo as suggested in the aforesaid research paper.
2. : The main class where we create the objects of all other classes and call functions to read the input file, generate sets, calculate largeItem sets according to minSupport and generate rules based on the found largeItem sets and the minConfidence provided.
3.It mainly has functions for generating sets for confidence keeping in mind the min_conf.
4. We read the CSV file, store the itemsets in hashmaps (for mapping the set of words in a large item set with its count) and calculate the support for each of them. We further prune down the item sets with those satisfying the minSupport. 

Command Line specification:

You must specify the minimum support and minimum confidence along with the CSV file (INTEGRATED-DATASET.csv).

Most Interesting example run:
Run as <INTEGRATED-DATASET.csv> <0.07> <0.1>

The reason for it being interesting is explained above in the 4 points and the paragraph that follows these points.
