del *.class
javac -classpath C:\GitHub\JActor2\jactor2-core\target\jactor2-core-0.8.2.jar;slf4j-api-1.7.5.jar -Xlint:deprecation -Xlint:unchecked *.java
java -Djactor.debug=true -classpath C:\GitHub\JActor2\jactor2-core\target\jactor2-core-0.8.2.jar;slf4j-api-1.7.5.jar;slf4j-simple-1.7.5.jar;guava-15.0.jar;. %1
del *.class
