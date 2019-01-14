# Akka-Websocket
On Start of this app, Kafka Producer is triggered and starts producing messages to an existing kafka topic. Then an inbuilt consumer will start consuming those messages from te earliest offset and process them by directing a custom signal to the connected users through websocket.

## How to use
Run kafka server and create a topic(if a topic already exists skip this)

Open application.conf and change the property "topic" to your topic name and bootstrap.servers to your kafka-server host and port numbers

Run the application (eg:sbt run)

Connect to localhost:8080 using any browser. Then enter user name(some random name) and password("password") and hit login

Now you will be redirected another page and a connected message will be poped. After connecting, all the messages produced by Kafka producer are processed and sent to the browser.(Every new message will be shown in every 30 seconds approx)

You can open the url localhost:8080 in mutiple browser windows/tabs and login with different names. In this case Producer will randomly push messages to the connected users
