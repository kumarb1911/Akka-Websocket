# Akka-Websocket
On Start of this app, Kafka Producer is triggered and starts producing messages to an existing kafka topic. Then an inbuilt consumer will start consuming those messages from te earliest offset and process them by directing a custom signal to the connected users through websocket.

## How to use
Run kafka server and create a topic(if a topic already exists skip this)

Open application.conf and change the property "topic" to your topic name.

Run the application (eg:sbt run)

Open any websocket client (eg: Simple Websocket Client crome extension), enter ws://localhost:8080/join/<SomeThing refered as userid> and ten open connection.

Now all the consumer processed and directed messages sould be visible in the message log area.
