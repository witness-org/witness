package com.witness.server.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * The standard {@link Email} validation constraint is very liberal, i.e. the regular expression used to verify the syntax of email addresses is '.*'.
 * The Firebase authentication server, however, enforces a stricter syntax for email addresses. Although it is unfortunately not fully transparent as
 * to what constitutes a valid email address, this annotation catches the most common irregularities. The pattern it uses, "^[^@]+@[^@]+\.[^@]{2,}$",
 * makes mandatory the occurrence of "@" as well as at least one "non-@" character before it, followed by at least one character, a period (".") and,
 * finally, at least two characters (which are not equal to "@"). Note that this pattern does not exclude special characters in the email address
 * such as "ö", "Ä" or "ß" which are deemed invalid by Firebase Auth. Due to the lack of transparency, it is difficult to obtain an exhaustive list
 * of illegal characters. Therefore, the pattern is kept as simple as possible - it should suffice for the vast majority of cases.
 * </p>
 * <p>
 * Note that the values of the properties {@link EmailStrict#message()}, {@link EmailStrict#groups()}, {@link EmailStrict#payload()} do not have
 * any effect. The validation functionality is thoroughly delegated to the {@link Email} annotation with aforementioned pattern. These properties
 * are merely present in order to satisfy the contract of {@link Constraint}, which is necessary for {@link EmailStrict} to be considered when
 * automatic validation is triggered.
 * </p>
 */
@SuppressWarnings("unused") // Unused properties are deliberately unused because they are contractually required, but their values are ignored.
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Email(regexp = "^[^@]+@[^@]+\\.[^@]{2,}$",
    message = "{jakarta.validation.constraints.Email.message}: contain '@' and at least one other character before and after it")
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
