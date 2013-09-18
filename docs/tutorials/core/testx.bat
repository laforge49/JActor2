del *.class
javac -classpath C:\GitHub\JActor2\jactor2-core\target\jactor2-core-0.3.0.jar;slf4j-api-1.7.5.jar *.java
java -classpath C:\GitHub\JActor2\jactor2-core\target\jactor2-core-0.3.0.jar;slf4j-api-1.7.5.jar;slf4j-simple-1.7.5.jar;. %1
