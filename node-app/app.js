const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 8090;

app.set('view engine', 'pug');
app.set('views', path.join(__dirname, 'views'));

app.use(express.json());

app.get('/', (req, res) => {
  res.render('home');
});

app.post('/log', (req, res) => {
  console.log('[client]', JSON.stringify(req.body));
  res.status(204).end();
});

app.get('/healthz', (req, res) => res.json({ status: 'ok' }));

app.listen(PORT, '0.0.0.0', () => {
  console.log(`node-app listening on ${PORT}`);
});
