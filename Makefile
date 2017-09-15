# javac -cp contest.jar player1.java <OTHER_SOURCE_FILES>
# jar cmf MainClass.txt submission.jar player1.class <OTHER_CLASS_FILES>
# java -jar testrun.jar -submission=player1 -evaluation=BentCigarFunction -seed=1

JC=javac
JCFLAGS=-cp contest.jar
PLAYER=player109

SOURCES=$(PLAYER).java $(filter-out $(PLAYER).java, $(wildcard *.java))
CLASSES=$(SOURCES:.java=.class)

JAR=jar
JARFLAGS=cmf
SUBMISSION=submission.jar
TEST_JAR=testrun.jar
MANIFEST=MainClass.txt

JVM=java
DJAVA=-Djava.library.path="."
FUNC=BentCigarFunction
SEED=1
JRFLAGS=-$(JAR) $(TEST_JAR) -submission=$(PLAYER) -evaluation=$(FUNC) -seed=$(SEED)
TMP=tmp

all: submission

submission: $(CLASSES)
	$(JAR) $(JARFLAGS) $(MANIFEST) $(SUBMISSION) $(CLASSES)

%.class: %.java
	$(JC) $(JCFLAGS) $<

run:
	$(JVM) $(DJAVA) $(JRFLAGS)

clean:
	rm -rf $(CLASSES) $(SUBMISSION) $(TMP)
