all: player109.class submission.jar

player109.class: player109.java
	javac -cp contest.jar player109.java

submission.jar:
	jar cmf MainClass.txt submission.jar player109.class

clean:
	rm -f player109.class submission.jar
