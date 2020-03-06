/**
 * Copyright (C) 2020 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
