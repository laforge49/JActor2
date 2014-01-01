del *.class
javac -classpath C:\GitHub\JActor2\jactor2-core\target\jactor2-core-0.8.0.jar;slf4j-api-1.7.5.jar *.java
java -classpath C:\GitHub\JActor2\jactor2-core\target\jactor2-core-0.8.0.jar;slf4j-api-1.7.5.jar;slf4j-simple-1.7.5.jar;pcollections-2.1.2.jar;guava-15.0.jar;. %1
del *.class
