package common;

import lombok.Builder;

@Builder
public record File(String name, int size) {

}
