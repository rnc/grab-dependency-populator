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
@GrabResolver(name="jenkins-releases", root="https://repo.jenkins-ci.org/releases/")
@Grapes([
        @Grab("com.github.everit-org.json-schema:org.everit.json.schema:1.11.0"),
        @Grab("org.jenkins-ci.main:jenkins-core:2.164"),
        @Grab("info.picocli:picocli-groovy:4.2.0")
])
@GrabExclude("org.codehaus.groovy:groovy-all")

class ValidateDefinitionCli implements Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    @Override
    void run() {

    }
}
