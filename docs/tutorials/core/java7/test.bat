del *.class
javac -classpath jactor2-core-0.10.0.jar;jactor2-coreMt-0.10.0.jar;slf4j-api-1.7.5.jar *.java
java -classpath jactor2-core-0.10.0.jar;jactor2-coreMt-0.10.0.jar;slf4j-api-1.7.5.jar;slf4j-simple-1.7.5.jar;guava-15.0.jar;pcollections-2.1.2.jar;metrics-core-3.0.1.jar;metrics-core-3.0.1.jar;. %1
