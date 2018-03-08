**Usage** 

    @Parameter(names = {"-h", "-?", "--help"}, descriptionKey = "help", help = true)
    @Parameter(names = {"-f", "--file"}, description = "The path to get the file to up", required = true)
    @Parameter(names = {"-n", "--folderName"}, description = "The name of the targeted Box folder", required = true)
    
**Dependencies**

_With JAVA 1.8_161 and above_
 - Download the bc jar file corresponding to your Java version ([Link](https://www.bouncycastle.org/fr/latest_releases.html))
 - Copy the file in your lib/ext folder: jdk1.8.0_161.jdk/Contents/Home/jre/lib/ext
 - In the JRE/lib/security folder 
 - Add this provider: security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
 - Adapt the ID for your system  

_With an earlier Java version_

Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 If you don't install this, you'll get an exception about key length or exception about parsing PKCS private key for Box Developer Edition. This is not a Box thing, this is a U.S. Government requirement concerning strong encryption. The listed jar is for Oracle JRE. There might be other similar JARs for diffenrent JRE versions like the one below for IBM JDK Java Cryptography Extension for IBM JDK

Download the file and follow the instructions: 

[Java 7](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html)

[Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)