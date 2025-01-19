import fetch from 'node-fetch';
import express from 'express';
import pa11y from 'pa11y';
import cors from 'cors';

const app = express();

app.use(express.json());
app.use(cors());

app.post('/analyze', async (req, res) => {
  let response = await pa11y(req.body.url);
  res.send(response);
});

app.post('/caption', (req, res) => {
  fetch("https://api-inference.huggingface.co/models/meta-llama/Llama-3.2-11B-Vision-Instruct/v1/chat/completions", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      model: "meta-llama/Llama-3.2-11B-Vision-Instruct",
      messages: [
        {
          role: "user",
          content: [
            {
              type: "text",
              text: "Describe this image in 15 words."
            },
            {
              type: "image_url",
              image_url: {
                url: req.body.url
              }
            }
          ]
        }
      ],
      max_tokens: 500,
      stream: false
    })
  }).then(res => {
    if (res.ok)
      return res.json();
    throw new Error("Failed to fetch");
  })
    .then(json => {
      res.send(json.choices[0].message.content);
    })
    .catch((e) => {
      res.status(400);
      res.send();
    });
});

app.listen(3000, () => {
  console.log('Server is running on http://localhost:3000');
});