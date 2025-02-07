package common;

import lombok.Builder;

import java.util.Objects;

@Builder
public record File(String name, int size, String content) {

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        File file = (File) object;
        return Objects.equals(name, file.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
