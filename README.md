hokuyo-java-lib
===============

How to use (maven)
------------------

Simply. Add to `pom.xml`.

    <repositories>
        <repository>
            <id>hokuyo-java-lib-mvn-repo</id>
            <url>https://github.com/dev-hokuyo/hokuyo-java-lib/raw/mvn-repo</url>
        </repository>
    </repositories>

Then, add dependencies.

    <dependencies>
        <dependency>
            <groupId>hokuyo</groupId>
            <artifactId>hokuyo-lib</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
