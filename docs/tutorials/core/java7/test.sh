rm -fr *.class
javac -cp jactor2-core-0.11.0.jar:jactor2-coreMt-0.11.0.jar:slf4j-api-1.7.7.jar *.java
java -cp jactor2-core-0.11.0.jar:jactor2-coreMt-0.11.0.jar:slf4j-api-1.7.7.jar:slf4j-simple-1.7.7.jar:guava-15.0.jar:pcollections-2.1.2.jar:metrics-core-3.0.1.jar:. $1
