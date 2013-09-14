test.sh is
rm -fr *.class
javac -cp jactor2-core-0.2.0.jar:slf4j-api-1.7.5.jar *.java
java -cp jactor2-core-0.2.0.jar:slf4j-api-1.7.5.jar:slf4j-simple-1.7.5.jar:. $1
