package DatabaseModel.Annotations;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface PrimaryKey {
	boolean nullable = false;
}
