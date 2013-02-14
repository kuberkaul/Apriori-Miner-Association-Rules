default: AprioriApp.class
AprioriApp.class: src/AprioriMiner.java
	javac -sourcepath src/ -d bin/ src/*.java

# AprioriMiner.java is the file which has the main() function of the project. 
# The above command compiles AprioriMiner.java.
# Create a directory named src and put the main AprioriMiner.java file in it. This is specified by -sourcepath. 
# Create an empty directory named bin where all compiled *.class files will be created. This is specified by -d. 		
# This file should be in the directory that contains src, lib and bin directories. 
# To compile type "make" and it should compile the entire project. 

run:
	java AprioriMiner "$(ARG1)" "$(ARG2)" "$(ARG3)"
		
# The above command specifies how to run AprioriMiner along with the input arguments. Replace them with number of required arguments.
# To run your project type "make run" and it should run the project. 
