del *.class
javac -classpath jactor2-core-0.0.4.jar;slf4j-api-1.7.5.jar *.java
java -classpath jactor2-core-0.0.4.jar;slf4j-api-1.7.5.jar;slf4j-simple-1.7.5.jar;. %1
