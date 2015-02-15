rm -fr *.class
javac -cp jactor2-core-1.0.0.jar:jactor2-coreMt-1.0.0.jar:slf4j-api-1.7.7.jar *.java
java -cp jactor2-core-1.0.0.jar:jactor2-coreMt-1.0.0.jar:slf4j-api-1.7.7.jar:slf4j-simple-1.7.7.jar:guava-18.0.jar:. $1
