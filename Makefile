
.PHONY: all clean run

# Specify the package directory
PACKAGE_DIR = cse360

# Compile all Java files in the cse360 package
all: 
	javac $(PACKAGE_DIR)/*.java

# Run the Main class
run: all
	java $(PACKAGE_DIR).Main

# Clean up the class files
clean:
	rm -f $(PACKAGE_DIR)/*.class
