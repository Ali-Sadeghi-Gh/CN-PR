package common;

import lombok.Builder;

import java.util.Set;

@Builder
public record GetFileResult(boolean found, File file, Set<String> addresses) {

}
