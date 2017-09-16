# javac -cp contest.jar player1.java <OTHER_SOURCE_FILES>
# jar cmf MainClass.txt submission.jar player1.class <OTHER_CLASS_FILES>
# java -jar testrun.jar -submission=player1 -evaluation=BentCigarFunction -seed=1

JC=javac
JCFLAGS=-cp contest.jar
PLAYER=player109

SOURCES=$(PLAYER).java $(filter-out $(PLAYER).java, $(wildcard *.java))
CLASSES=$(SOURCES:.java=.class)

JAR=jar
JARCFLAGS=cmf
SUBMISSION=submission.jar
MANIFEST=MainClass.txt

JVM=java
DJAVA=-Djava.library.path="."
JARRFLAGS=-$(JAR) testrun.jar
RTARGS=-submission=$(PLAYER) -evaluation=$(FUNC) -seed=$(SEED)
FUNC=BentCigarFunction
SEED=1

all: submission

submission: $(CLASSES)
	$(JAR) $(JARCFLAGS) $(MANIFEST) $(SUBMISSION) $(CLASSES)

$(CLASSES): $(SOURCES)
	$(JC) $(JCFLAGS) $(SOURCES)

run: submission
	$(JVM) $(DJAVA) $(JARRFLAGS) $(RTARGS)

clean:
	rm -rf $(CLASSES) $(SUBMISSION) tmp
