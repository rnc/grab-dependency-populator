

@GrabResolver(name='jitpack', root='https://jitpack.io/')
@Grab('com.github.everit-org.json-schema:org.everit.json.schema:1.11.0')
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

import groovy.json.JsonBuilder

@Grab('org.yaml:snakeyaml:1.21')
import org.yaml.snakeyaml.Yaml

@SuppressWarnings("unused")
class Foo {
    Yaml y
    JsonBuilder b
    SchemaLoader s
    JSONObject o
    JSONTokener t
    Schema sc
}
