# javac -cp contest.jar player1.java <OTHER_SOURCE_FILES>
# jar cmf MainClass.txt submission.jar player1.class <OTHER_CLASS_FILES>
# java -jar testrun.jar -submission=player1 -evaluation=BentCigarFunction -seed=1

SHELL=/bin/bash
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
SEED=$$RANDOM

FUNCTIONS=BentCigarFunction KatsuuraEvaluation SchaffersEvaluation

$(SUBMISSION): $(CLASSES)
	$(JAR) $(JARCFLAGS) $(MANIFEST) $@ $(CLASSES:.class=*.class)

$(CLASSES): $(SOURCES)
	$(JC) $(JCFLAGS) $(SOURCES)

.PHONY: all run runall clean clean-build
all: $(SUBMISSION)

run: $(SUBMISSION)
	@echo $(FUNCTION) "seed="$(SEED) ; \
	$(JVM) $(DJAVA) $(JARRFLAGS) $(RTARGS)

runall: $(SUBMISSION)
	@for f in $(FUNCTIONS) ; do \
		echo $$f "seed="$(SEED) ; \
		$(JVM) $(DJAVA) $(JARRFLAGS) $(RTARGS:$(FUNCTION)=$$f) ; \
		printf "\n" ; \
    done
	@printf "Complete!\n"

clean:
	rm -rf $(CLASSES) $(INNERCLASSES) $(SUBMISSION) tmp

clean-build: clean $(SUBMISSION)
