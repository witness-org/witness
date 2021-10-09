package com.witness.server.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Email;

/**
 * <p>
 * The standard {@link Email} validation constraint is very liberal, i.e. the regular expression used to verify the syntax of email addresses is '.*'.
 * This annotation enforces the pattern that is also required by a Firebase authentication server: "^[^@]+@[^@]+$", making mandatory the occurrence
 * of "@" as well has at least one "non-@" character before and after it.
 * </p>
 * <p>
 * Note that the values of the properties {@link EmailStrict#message()}, {@link EmailStrict#groups()}, {@link EmailStrict#payload()} does not have
 * any effect. The validation functionality is thoroughly delegated to the {@link Email} annotation with aforementioned pattern. These properties
 * are merely present in order to satisfy the contract of {@link Constraint}, which is necessary for {@link EmailStrict} to be considered when
 * automatic validation is triggered.
 * </p>
 */
@SuppressWarnings("unused") // Unused properties are deliberately unused because they are contractually required, but their values are ignored.
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Email(regexp = "^[^@]+@[^@]+$",
    message = "{javax.validation.constraints.Email.message}: contain '@' and at least one other character before and after it")
public @interface EmailStrict {
  /**
   * Message in case of a validation error. The value of this member is ignored since it is defined purely to fulfill the contract of
   * {@link Constraint}, see also documentation comments of {@link EmailStrict}.
   *
   * @return an error message
   */
  String message() default "";

  /**
   * Targeted groups. The value of this member is ignored since it is defined purely to fulfill the contract of
   * {@link Constraint}, see also documentation comments of {@link EmailStrict}.
   *
   * @return an error of validation groups
   */
  Class<?>[] groups() default {};

  /**
   * Provides opportunities for extensibility. The value of this member is ignored since it is defined purely to fulfill the contract of
   * {@link Constraint}, see also documentation comments of {@link EmailStrict}.
   *
   * @return an error message
   */
  Class<? extends Payload>[] payload() default {};
}
