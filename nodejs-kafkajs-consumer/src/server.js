// initialize tracer
const CLSContext = require('zipkin-context-cls');
const { Tracer } = require('zipkin');
const { recorder } = require('./recorder');
const { Kafka } = require('kafkajs');

const ctxImpl = new CLSContext('zipkin');
const localServiceName = process.env.SERVICE_NAME;

const kafkaTopic = process.env.KAFKA_TOPIC;
const kafkaClientId = process.env.KAFKA_CLIENTID;
const kafkaBrokerUrl = process.env.KAFKA_BROKER_URL;
const kafkaRemoteServiceName = process.env.KAFKA_REMOTE_SERVICE_NAME;
const kafkaConsumerGroupId = process.env.KAFKA_CONSUMER_GROUP_ID;

const tracer = new Tracer({
	ctxImpl,
	recorder: recorder(localServiceName),
	localServiceName
});

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

const consumer = kafka.consumer({ groupId: kafkaConsumerGroupId });
consumer.connect();
consumer.subscribe({ topic: kafkaTopic });
consumer.run({
	eachMessage: async ({ topic, partition, message }) => {
		console.log({ topic, partition, value: message.value.toString() });
	}
});
