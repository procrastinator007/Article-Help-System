
# Makefile to compile and run the Java project

JAVAC = javac
JAVA = java
SRC = cse360/*.java
MAIN = cse360.Main

# Default rule to compile the code
all:
	$(JAVAC) $(SRC)

# Run the main class
run:
	$(JAVA) $(MAIN)

# Clean all temporary files except the database file
clean:
	find . -name "*.class" -delete
	rm -f *.tmp
	rm -f *.log

# Clean all files, including the database
clean-all: clean
	rm -f database.ser

# Phony targets to avoid conflicts with file names
.PHONY: all run clean clean-all
