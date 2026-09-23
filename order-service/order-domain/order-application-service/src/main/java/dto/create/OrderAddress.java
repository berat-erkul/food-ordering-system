package dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class OrderAddress {

    @NotNull
    @Size(max = 50)
    private final String street;
    @NotNull
    @Size(max = 10)
    private final String postalCode;
    @NotNull
    @Size(max = 50)
    private final String city;
}
