
@GrabResolver(name='jitpack', root='https://jitpack.io/')
@Grab('com.github.everit-org.json-schema:org.everit.json.schema:1.11.0')

import org.everit.json.schema.FormatValidator

@Grab('org.yaml:snakeyaml:1.01')
import org.yaml.snakeyaml.Yaml

@SuppressWarnings("unused")
class CronTabValidator implements FormatValidator {
    @Override
    Optional<String> validate(final String subject) {
        @SuppressWarnings("unused")
        Yaml y
        return Optional.empty()
    }
}
