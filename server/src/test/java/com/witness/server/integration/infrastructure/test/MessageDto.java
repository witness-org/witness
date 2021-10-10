package com.witness.server.integration.infrastructure.test;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
  @NotNull
  @Min(200)
  @Max(250)
  private Long id;

  @Length(min = 10)
  @NotBlank
  private String content;
}
