# tiqr-javacard
Java Card implementation of tiqr


## Install Java Card Development Kit


http://www.oracle.com/technetwork/java/embedded/javacard/

http://www.oracle.com/technetwork/java/embedded/javacard/downloads/default-1970005.html


http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-javame-419430.html#java_card_kit-2.2.2-oth-JPR

 Java Card Development Kit 2.2.2 Linux  
 
java_card_kit-2_2_2-linux.zip
 

4 bundles

        mkdir jck222
        unzip -d jck222/ java_card_kit-2_2_2-linux/java_card_kit-2_2_2/*.zip 

# JC kit

	unzip java_card_kit-2_2_2-linux/java_card_kit-2_2_2/java_card_kit-2_2_2-rr-bin-linux-do.zip 


## build

	export JAVA_HOME=$(/usr/libexec/java_home)


# applet

	javac -source 1.3 -target 1.1 -cp lib/api.jar src/applet/CalcApplet.java 
	sh bin/converter  -v -out CAP -exportpath api_export_files -classdir src -applet 0x3B:0x29:0x63:0x61:0x6C:0x63:0x01 applet.CalcApplet applet 0x3B:0x29:0x63:0x61:0x6C 1.0

# GP

	wget -q https://github.com/martinpaljak/GlobalPlatformPro/releases/download/v0.3.9/gp.jar
	java -jar gp.jar -install src/applet/javacard/applet.cap
	java -jar gp.jar -l

# CAD

	javac src/terminal/CalcTerminal.java 
	java -cp src terminal.CalcTerminal

# 

	java -jar gp.jar -delete 3B2963616C

