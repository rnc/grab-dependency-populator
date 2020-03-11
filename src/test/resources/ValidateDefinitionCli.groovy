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
