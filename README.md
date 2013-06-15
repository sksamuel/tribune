Redis River Plugin for ElasticSearch
=========================

| Redis Driver Plugin | ElasticSearch | Redis |
| ------ | --------- | --------- |
| 0.90.1.x | 0.90.1 | 2.6.x |

[![Build Status](https://travis-ci.org/sksamuel/elasticsearch-river-redis.png)](https://travis-ci.org/sksamuel/elasticsearch-river-redis)

## How to use

Start the Redis river by curling a document like the following to the river index.

```
curl -XPUT 'http://localhost:9200/_river/redisriver/_meta' -d '{
    "type": "redis",
    "redis": {
        "hostname": "REDIS_HOSTNAME",
        "port" : "REDIS_PORT_OPTIONAL",
        "json" : false,
        "messageField" : "content",
		"channels" : "channel1,channel2"
    },
    "index": {
        "name": "INDEX_NAME",
    }
}'
```

The following parameters are available in the rediss river document.

| Parameter | Description |
| ------ | --------- |
| redis.hostname | The hostname of the redis server. Optional. Defaults to _localhost_.
| redis.port | The port of the redis server. Optional. Defaults to _6379_.
| redis.password | The password of the redis server. Optional. Defaults to no password.
| redis.database | The redis database. Optional. Defaults to the first database.
| redis.channels | The pubsub channels that the river should subscribe to seperated by commas. Set to "*" to listen to all channels. Optional. Defaults to all.
| redis.json | If set to true then the messages received must be elastic syntax valid json messages. If set to false then the entire message is indexed as text into a single field. Optional. Defaults to false. |
| redis.messageField | If json is set to false then the entire message will be indexed inside a field of this name. Optional. Defaults to _content_.
| index.name | The name of the index where documents should be indexed. Optional. Defaults to _redis-index_.

Then any messages published on the channels that the river is subscribed to will be automatically indexed. The index name is taken from the index.name parameter in the settings, and the type is taken as the channel name.

## Example

Start elastic search locally on port 9200.

Start Redis running locally on 6379.

Curl this document to start the river module:

```
curl -XPUT 'http://localhost:9200/_river/redisriver/_meta' -d '{
    "type": "redis",
    "redis": {
        "hostname": "localhost",
        "port" : "6379",
        "json" : false,
        "messageField" : "content",
        "channels": "test-channel"
    },
    "index": {
        "name": "test-redis",
    }
}'
```

Connect to the redis command line and publish a test document:

```
$ redis-cli
redis 127.0.0.1:6379> PUBLISH test-channel "I love redis me"
(integer) 0
```
Then the content "I love redis me" would be indexed in the _test-redis_ with type _test-channel_.
The actual body of the request to elastic search would look like this:

```
{
   "content" : "I love redis me",
   "timestamp" : <timestamp message received>
}
```

Query elastic to see the document

```
curl -XGET 'http://localhost:9200/test-redis/_search?q=content:redis'
```

## License
```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Stephen Samuel

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```