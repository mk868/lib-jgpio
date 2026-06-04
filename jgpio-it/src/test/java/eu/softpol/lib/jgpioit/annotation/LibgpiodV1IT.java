package eu.softpol.lib.jgpioit.annotation;

import eu.softpol.lib.jgpioit.ItTags;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag(ItTags.CONTAINER_IT)
@Tag(ItTags.LIBGPIOD_V1)
public @interface LibgpiodV1IT {

}
