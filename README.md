# WebCrawler #
Test task for Scalable Capital


As task asked to use minimum of 3rd party I have choosen native java http client (in jdk since Java 11 (LTS)) to handle async http calls

Only Lombok (for @Slf4j and @AllArgsConstructor),Jsoup and some test 3rd-party libraries are used.


Also, this task would be mach more simplier to implement using Kotlin and Ktor (https://ktor.io/) basically because of awesome coroutines in this language


### How to use ###

git clone git@github.com:meelvin182/WebCrawler.git
cd WebCrawler
./gradlew.sh clean build
cd build/libs
java -jar *.jar %INSERT QUERY PARAM%
or
java -jar *.jar
