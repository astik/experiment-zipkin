// initialize tracer
const axios = require('axios');
const express = require('express');
const CLSContext = require('zipkin-context-cls');
const { Tracer } = require('zipkin');
const { recorder } = require('./recorder');

const ctxImpl = new CLSContext('zipkin');
const localServiceName = process.env.SERVICE_NAME;
const remoteServiceUrl = process.env.REMOTE_SERVICE_URL;
const serverPort = process.env.SERVER_PORT;

const tracer = new Tracer({
	ctxImpl,
	recorder: recorder(localServiceName),
	localServiceName
});

const app = express();

// instrument the inner client
const wrapAxios = require('zipkin-instrumentation-axiosjs');
const zipkinAxios = wrapAxios(axios, { tracer });

// instrument the server
const zipkinMiddleware = require('zipkin-instrumentation-express')
	.expressMiddleware;
app.use(zipkinMiddleware({ tracer }));

app.get('/', (req, res) => {
	zipkinAxios
		.get(remoteServiceUrl)
		.then(response => res.send(response.data))
		.catch(err => {
			console.error('Error', err.response ? err.response.status : err.message);
			res.status(err.response ? err.response.status : 502);
		});
});

app.get('/fibonacci', (req, res) => {
	const count = Math.min(req.query.count || 5, 42);
	zipkinAxios
		.get(`${remoteServiceUrl}/fibonacci?count=${count}`)
		.then(response => res.send(response.data))
		.catch(err => {
			console.error('Error', err.response ? err.response.status : err.message);
			res.status(err.response ? err.response.status : 502);
		});
});

app.listen(serverPort, () => {
	console.log(`Backend listening on port ${serverPort}!`);
});
