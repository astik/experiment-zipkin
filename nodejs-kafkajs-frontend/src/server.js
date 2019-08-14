// initialize tracer
const express = require('express');
const CLSContext = require('zipkin-context-cls');
const { Tracer } = require('zipkin');
const { recorder } = require('./recorder');
const { Kafka } = require('kafkajs');

const ctxImpl = new CLSContext('zipkin');
const localServiceName = process.env.SERVICE_NAME;
const serverPort = process.env.SERVER_PORT;

const kafkaTopic = process.env.KAFKA_TOPIC;
const kafkaClientId = process.env.KAFKA_CLIENTID;
const kafkaBrokerUrl = process.env.KAFKA_BROKER_URL;
const kafkaRemoteServiceName = process.env.KAFKA_REMOTE_SERVICE_NAME;

const tracer = new Tracer({
	ctxImpl,
	recorder: recorder(localServiceName),
	localServiceName
});

const app = express();

// instrument the server
const zipkinMiddleware = require('zipkin-instrumentation-express')
	.expressMiddleware;
app.use(zipkinMiddleware({ tracer }));

// instrument the kafka client
const kafka = require('zipkin-instrumentation-kafkajs')(
	new Kafka({
		clientId: kafkaClientId,
		brokers: [kafkaBrokerUrl]
	}),
	{
		tracer, // Your zipkin tracer instance
		remoteServiceName: kafkaRemoteServiceName // This should be the symbolic name of the broker, not a consumer.
	}
);

app.get('/', (req, res) => {
	const message = req.query.message || 'Hello world from kafka producer';
	sendMessageToKafka(message).then(() => {
		res.send('OK');
	});
});

app.listen(serverPort, () => {
	console.log(`Backend listening on port ${serverPort}!`);
});

async function sendMessageToKafka(message) {
	const producer = kafka.producer();
	await producer.connect();
	return producer.send({
		topic: kafkaTopic,
		messages: [
			{ key: 'key1', value: JSON.stringify({ date: new Date(), message }) }
		]
	});
}
