# javac -cp contest.jar player1.java <OTHER_SOURCE_FILES>
# jar cmf MainClass.txt submission.jar player1.class <OTHER_CLASS_FILES>
# java -jar testrun.jar -submission=player1 -evaluation=BentCigarFunction -seed=1

JC=javac
JCFLAGS=-cp contest.jar
PLAYER=player50

SOURCES=$(PLAYER).java $(filter-out $(PLAYER).java, $(wildcard *.java))
CLASSES=$(SOURCES:.java=.class)

JAR=jar
JARCFLAGS=cmf
SUBMISSION=submission.jar
MANIFEST=MainClass.txt

JVM=java
DJAVA=-Djava.library.path="."
JARRFLAGS=-$(JAR) testrun.jar
RTARGS=-submission=$(PLAYER) -evaluation=$(FUNCTION) -seed=$(SEED)

FUNC=BentCigarFunction
FUNCTION=$(subst .class,,$(FUNC))
SEED=1

all: $(SUBMISSION)

$(SUBMISSION): $(CLASSES)
	$(JAR) $(JARCFLAGS) $(MANIFEST) $(SUBMISSION) $(CLASSES)

$(CLASSES): $(SOURCES)
	$(JC) $(JCFLAGS) $(SOURCES)

run: $(SUBMISSION)
	$(JVM) $(DJAVA) $(JARRFLAGS) $(RTARGS)

.PHONY: clean
clean:
	rm -rf $(CLASSES) $(SUBMISSION) tmp
