# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.


## Sequence Diagram (*Phase 2*)
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=C4S2BsFMAIBUAsQGdrOgBXgQyTATAFAFYDGwA9gE7QDC4IkAdsNAQA5aWgkgfPQBlSJQBuw9p268s-ABIyAJlEoSuIHnxZDR6yKqmboAESzAsAQRIlISJAQWmsAIxwwFTogHpPtKJyIA5pTkAK5sAMRBWACe0OH0AfDAUbF0kJzQWGxs9CSmIOSMrHQM-AC0AHyCwmKUAFzQANpGAKIAMi2wLQC60J7u0KzatdCV0PKMSsINJH7U2JPKBBNT1GPDujNzABQAlAQb1qNVJmaW1rZb6ZTm4OB7BKcWVjYoY+4NCpBQwDBYIcB4EgADTQEK4Sgg6ABLAAWxsj0c51eo3WNU20C+P0gCgO6KOZTGK2UDQA3gBfZaKZSoqrDabQCl40TCWm+UrABp4AAM3KZTFxXh8ACVIAFkL8VEFQhEACwAZgAHHgAJwkOIJJJOcAhGCi8VIYCUfKFAglJgsNEs+pNdAAeQEsF6nnBwgAOoxSQAiV2URhwyBeupe6Be0Feji2ADuVAUQZDYdDkFhWBA4Hjocp9LWVWJDO9vv98IziYjOCQMcoceDofDydT6ZrXspeZz1R01galDFEuE231vcoooAjrrDftDjAxk9kZdoZBgABVCHbQsB-Yzl62Y6Ypx1ABiIEm0GXwie0CcsTX8IITm7WAA1ieIdAo2B4GCIUW-uB7wpYpAAAeEp2JuFxvFaIgYqelBPMyUEEpBDK3H+0SwI+TAtIB1hsKAprZmy5rMA0srcvKHrevCthYAEgYNF6LSUMENrXj+qHQGYD5MM2BACoiZxbhBdL4pADSMCEdzwboO5ga8dQkPevwwauEJPBuSKCTuHzmAoCjPmejhSQSJwaeB8mKZA5gAvA2z-ICan8c84FaXuOl6VZ9mGZObKtg0A6GsIopIBJwBUosrKEsJ1pkj6X4BiG9G1qGdnwLA5BcYwCWhpm8ERWMRGctAPLchRsXCN+WUJuGKVpRllU5XxRAEN40BtOQ4qMAQ0phOEVAyLRcT7jQLRGDQ5ite1R6ObObxRQhonQB58BwQVO7Zg0jT2o6zq4LYBSMBRoasfGpaRhWsYndAWY1BFubUvmR1xcWwaneWlbVl6IYtvdbaTg04CTYw2xtR1I5jsAE4iTJplyfOS4rqx6kCc57x7tAh7HjB56Xp+5UBre95PnDMHQN2wAhH6KDiZJsm2JF7bzdAYkSeARmQPTBF1Iu-rWVQIAAF44lhOF4Z12b0wVJHcgAjIdXpUUgNF0aGjHMQ0IQ84CfOC3GV28ZM02aRzIkNFjXlQ5BGK1CAABmsRne9bNrSb0AKekvxLSpePwpDHZTiZyOw1gumeylDm07Nu4NMHelh4ZEdsn9cdmE79O+RNoM2CFYWrInN02qSj3e3Rn3VdZtXcS9esERL9AWlyvJy8dL1l4CFeZVXlKNS1IOhKF3UREEkBMBqICJMk3Yj73AJmnX5RzQyzTtJ0PR9LtSD7R6mTl+lTC5W26fejVu8dwm33hb9LsAwEffA+1fdgzYEOpwHTmw7RwChzvGVI2-26o0zdGR5Y7WWxrEY+GVDYo0tp2benkU7eWnDDS4WIFyWWsrZb+TBf4zRcp8b4aC4HLUcBecBWCxYWyJD9Bo09gBBWzq2PO0VGTXWtIROehVioUS7gbZqPg2gSmgAAcQDHYAe4Rux6XCGwI8T4BGGmEaI2eHJnbMMaEIzozoYTwg9BAveNc7oXxino0+OVGEwIWvQQ02jIDAwlCI+Ej9xwv2MMgpADQP5fzbifHBRsqgfCAceZOWBSFEPblAlExs-bR1AebP20NA5ziEQuBxkAAByojtg2IyT7CJ-9-Fow-tCAMAZQnZPxgnKJ80PEBhyXoRBVCjGtXsQGeh4BQrmKQgXUMNikBBiaN6GxABJIw-TpZ4HlLKcMUZEBKSeiXUs2pSAPhghVFuPTakBheuSaA3RWEjFrhyBuJVPQbKov0xogyAwjLGRMqZoYZlgEgKs+K6yvRLJICs+ZJ1wzlPhNs3ZPDBQtRoBZRRN5xEKAAOwqm5JAbkcQWjyhoMNAAbLQMFqTigcNUbUDaW0nR9Bsbo8hFE-mQH2bdcY1DGRehMSWX5mznpnxzjSCxMwLI2O2KC92kBUlOOfog1+M0PELi8alHxeTI4BKKcE0JJipVMOqUQuCQrXGJPcXDVJIysnXKML4lGBTRUsBsWA4p8IRmKqqRiYZRhU6NNWA0Hlpg+WtKzu01leUF7dK9La25kzq753YUcoqjdTm+r1f62Uetu4+AAFLkCPOCvQA9R7j2iN8AGUZoAJqTVi8IHyHzKItLixe6BFyEs8MSzWEqMoURyDEYQNByAA0oKCW1lKD40qPuQhloYG0ZsoM21tfaI0Wo+mYn6uKMQACtE2MFSdsXNjAAj8sgKOJ+vtGZII1ca8V7cDWRKNYEkB8CQk4wVZU9lKq4nbuFYJY12qjC6otfqq1x6immpITjW1Vrr2-rVRHY16BwCNqHS2qg2wB1Nog5QQ9+So5w2gNB2C377agcHcOqgf65oYhQ1hlQDTDGOpzfO1dbrgoes6d6sknbg31y4Z6IFRAgA
## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
