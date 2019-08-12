// initialize tracer
const express = require('express');
const CLSContext = require('zipkin-context-cls');
const { Tracer } = require('zipkin');
const { recorder } = require('./recorder');

const ctxImpl = new CLSContext('zipkin');
const localServiceName = process.env.SERVICE_NAME;
const serverPort = process.env.SERVER_PORT;

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

app.get('/', (req, res) => {
	res.send(new Date().toString());
});

app.listen(serverPort, () => {
	console.log(`Backend listening on port ${serverPort}!`);
});
