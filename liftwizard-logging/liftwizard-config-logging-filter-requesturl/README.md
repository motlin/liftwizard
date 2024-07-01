```json
{
  "server": {
    "requestLog": {
      "appenders": [
        {
          "type": "console",
          "filterFactories": [
            {
              "type": "url",
              "urls": [
                "/manifest.json",
                "/assets-manifest.json",
                "/favicon.ico",
                "/service-worker.js"
              ]
            }
          ]
        }
      ]
    }
  }
}
```

